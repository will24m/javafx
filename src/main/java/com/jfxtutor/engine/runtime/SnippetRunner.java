package com.jfxtutor.engine.runtime;

import com.jfxtutor.engine.compile.CompileResult;
import com.jfxtutor.engine.compile.SnippetCompiler;
import com.jfxtutor.util.AppLog;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Owns the debounced compile-and-mount loop.
 *
 * The editor pushes source text through scheduleRecompile(...). After a short
 * quiet period, this runner compiles the snippet on a background thread, invokes
 * its generated build() method on a separate build thread, and finally applies
 * the result back on the JavaFX Application Thread.
 *
 * Only one SnippetSession is live at a time. When a newer successful snippet is
 * mounted, the previous session is closed so its old scene graph and bytecode
 * can be garbage-collected.
 */
public class SnippetRunner {

    private static final Duration DEBOUNCE = Duration.millis(350);
    private static final Duration BUILD_TIMEOUT = Duration.seconds(2);

    private final SnippetCompiler compiler = new SnippetCompiler();

    // Compilation happens off the JavaFX thread so typing and layout stay smooth.
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "snippet-compile");
        t.setDaemon(true);
        return t;
    });

    // build() may run arbitrary lesson code, so it gets its own timeout-able thread.
    private final ExecutorService buildExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "snippet-build");
        t.setDaemon(true);
        return t;
    });

    private final Consumer<Parent> onMount;
    private final Consumer<String> onError;
    private final PauseTransition debounce;

    // Each editor change increments the generation. Late results from older
    // generations are discarded instead of replacing the current preview.
    private final AtomicLong generation = new AtomicLong();
    private SnippetSession current;
    private String pendingSource;
    private volatile boolean stopped;

    public SnippetRunner(Consumer<Parent> onMount, Consumer<String> onError) {
        AppLog.info("runtime", "Starting SnippetRunner with debounced compile and isolated build executors.");
        this.onMount = onMount;
        this.onError = onError;
        this.debounce = new PauseTransition(DEBOUNCE);
        this.debounce.setOnFinished(e -> fire(generation.get()));
    }

    /** Editor calls this on every keystroke. */
    public void scheduleRecompile(String source) {
        if (stopped) return;
        this.pendingSource = source == null ? "" : source;
        long gen = generation.incrementAndGet();
        AppLog.info("runtime", "Queued debounced snippet compile #" + gen
                + " after editor change (" + this.pendingSource.length() + " characters).");
        debounce.playFromStart();
    }

    /** Skip debounce; useful for the initial mount after lesson load and Run button clicks. */
    public void recompileNow(String source) {
        if (stopped) return;
        this.pendingSource = source == null ? "" : source;
        long gen = generation.incrementAndGet();
        AppLog.info("runtime", "Starting immediate snippet compile #" + gen
                + " (" + this.pendingSource.length() + " characters).");
        debounce.stop();
        fire(gen);
    }

    private void fire(long gen) {
        String source = pendingSource;
        if (source == null) return;
        AppLog.info("runtime", "Submitting snippet compile #" + gen + " to background compiler thread.");
        executor.submit(() -> {
            BuildResult result;
            try {
                AppLog.info("runtime", "Compiler thread is compiling snippet #" + gen + ".");
                CompileResult compileResult = compiler.compile(source);
                result = compileResult.isSuccess()
                        ? buildWithTimeout(compileResult, gen)
                        : BuildResult.error(compileResult.formatDiagnostics());
                if (!compileResult.isSuccess()) {
                    AppLog.info("runtime", "Snippet #" + gen + " did not compile: "
                            + oneLine(compileResult.formatDiagnostics()));
                }
            } catch (Throwable t) {
                result = BuildResult.error("Compiler failure: " + describe(t));
                AppLog.info("runtime", "Snippet #" + gen + " hit an unexpected compiler failure: " + describe(t));
            }
            BuildResult finalResult = result;
            if (!stopped) {
                AppLog.info("runtime", "Compile/build result for snippet #" + gen
                        + " is ready; scheduling UI-thread apply.");
                Platform.runLater(() -> apply(gen, finalResult));
            } else {
                finalResult.close();
            }
        });
    }

    private BuildResult buildWithTimeout(CompileResult result, long gen) {
        AppLog.info("runtime", "Snippet #" + gen + " compiled; loading generated class "
                + result.getEntryClassName() + ".");
        SnippetClassLoader cl = new SnippetClassLoader(
                result.getClassBytes(), getClass().getClassLoader());
        Future<Object> future = buildExecutor.submit(() -> {
            AppLog.info("runtime", "Invoking static build() for snippet #" + gen + ".");
            Class<?> klass = cl.loadClass(result.getEntryClassName());
            Method build = klass.getMethod("build");
            return build.invoke(null);
        });

        try {
            Object out = future.get((long) BUILD_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!(out instanceof Parent root)) {
                cl.close();
                AppLog.info("runtime", "Snippet #" + gen + " build() returned a non-Parent value.");
                return BuildResult.error("build() must return a javafx.scene.Parent (got "
                        + (out == null ? "null" : out.getClass().getName()) + ")");
            }
            AppLog.info("runtime", "Snippet #" + gen + " build() returned "
                    + root.getClass().getSimpleName() + ".");
            return BuildResult.success(cl, root);
        } catch (TimeoutException e) {
            future.cancel(true);
            cl.close();
            AppLog.info("runtime", "Snippet #" + gen + " build() timed out after "
                    + (long) BUILD_TIMEOUT.toSeconds() + " seconds.");
            return BuildResult.error("Runtime error in build(): timed out after "
                    + (long) BUILD_TIMEOUT.toSeconds() + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            cl.close();
            AppLog.info("runtime", "Snippet #" + gen + " build() was interrupted.");
            return BuildResult.error("Runtime error in build(): interrupted");
        } catch (ExecutionException e) {
            cl.close();
            AppLog.info("runtime", "Snippet #" + gen + " threw during build(): " + describe(unwrap(e)));
            return BuildResult.error("Runtime error in build(): " + describe(unwrap(e)));
        }
    }

    private void apply(long gen, BuildResult result) {
        // Only the newest generation is allowed to update the preview. If an old
        // compile finishes late, closing its classloader prevents stale bytecode
        // from being retained.
        if (stopped) {
            result.close();
            return;
        }
        if (gen != generation.get()) {
            AppLog.info("runtime", "Discarding stale snippet #" + gen
                    + "; newer generation is #" + generation.get() + ".");
            result.close();
            return;
        }
        if (!result.isSuccess()) {
            AppLog.info("runtime", "Applying error result for snippet #" + gen + ".");
            onError.accept(result.error());
            return;
        }
        try {
            SnippetSession previous = current;
            SnippetSession next = new SnippetSession(result.classLoader(), result.root());
            AppLog.info("runtime", "Mounting successful snippet #" + gen + " on the JavaFX thread.");
            onMount.accept(result.root());
            current = next;
            if (previous != null) {
                AppLog.info("runtime", "Closing previous snippet session after successful replacement.");
                previous.close();
            }
        } catch (Throwable t) {
            result.close();
            AppLog.info("runtime", "Applying snippet #" + gen + " failed on UI thread: " + describe(t));
            onError.accept("Runtime error in build(): " + describe(t));
        }
    }

    public void shutdown() {
        AppLog.info("runtime", "SnippetRunner shutdown: stopping debounce timer and background executors.");
        stopped = true;
        debounce.stop();
        if (current != null) {
            AppLog.info("runtime", "Closing active snippet session.");
            current.close();
            current = null;
        }
        executor.shutdownNow();
        buildExecutor.shutdownNow();
    }

    private static Throwable unwrap(Throwable t) {
        Throwable current = t;
        while (current instanceof ExecutionException
                || current instanceof InvocationTargetException) {
            Throwable cause = current.getCause();
            if (cause == null) {
                break;
            }
            current = cause;
        }
        return current;
    }

    private static String describe(Throwable t) {
        Throwable cause = unwrap(t);
        String message = cause.getMessage();
        return cause.getClass().getSimpleName()
                + (message == null || message.isBlank() ? "" : ": " + message);
    }

    private static String oneLine(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ").trim();
    }

    private record BuildResult(SnippetClassLoader classLoader, Parent root, String error) {
        static BuildResult success(SnippetClassLoader classLoader, Parent root) {
            return new BuildResult(classLoader, root, null);
        }

        static BuildResult error(String error) {
            return new BuildResult(null, null, error);
        }

        boolean isSuccess() {
            return error == null;
        }

        void close() {
            if (classLoader != null) {
                classLoader.close();
            }
        }
    }
}
