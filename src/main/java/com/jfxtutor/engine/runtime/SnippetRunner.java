package com.jfxtutor.engine.runtime;

import com.jfxtutor.engine.compile.CompileResult;
import com.jfxtutor.engine.compile.SnippetCompiler;
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
 * Owns the debounced compile-and-mount loop. The editor pushes source text via
 * {@link #scheduleRecompile(String)}; after a quiet period we run the compile on
 * a background thread, then mount the produced {@link Parent} (or render an
 * error) on the FX thread. Only one {@link SnippetSession} is live at a time —
 * the previous one is closed when a new one mounts.
 */
public class SnippetRunner {

    private static final Duration DEBOUNCE = Duration.millis(350);
    private static final Duration BUILD_TIMEOUT = Duration.seconds(2);

    private final SnippetCompiler compiler = new SnippetCompiler();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "snippet-compile");
        t.setDaemon(true);
        return t;
    });
    private final ExecutorService buildExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "snippet-build");
        t.setDaemon(true);
        return t;
    });

    private final Consumer<Parent> onMount;
    private final Consumer<String> onError;
    private final PauseTransition debounce;

    private final AtomicLong generation = new AtomicLong();
    private SnippetSession current;
    private String pendingSource;
    private volatile boolean stopped;

    public SnippetRunner(Consumer<Parent> onMount, Consumer<String> onError) {
        this.onMount = onMount;
        this.onError = onError;
        this.debounce = new PauseTransition(DEBOUNCE);
        this.debounce.setOnFinished(e -> fire(generation.get()));
    }

    /** Editor calls this on every keystroke. */
    public void scheduleRecompile(String source) {
        if (stopped) return;
        this.pendingSource = source == null ? "" : source;
        generation.incrementAndGet();
        debounce.playFromStart();
    }

    /** Skip debounce — useful for the initial mount after lesson load. */
    public void recompileNow(String source) {
        if (stopped) return;
        this.pendingSource = source == null ? "" : source;
        long gen = generation.incrementAndGet();
        debounce.stop();
        fire(gen);
    }

    private void fire(long gen) {
        String source = pendingSource;
        if (source == null) return;
        executor.submit(() -> {
            BuildResult result;
            try {
                CompileResult compileResult = compiler.compile(source);
                result = compileResult.isSuccess()
                        ? buildWithTimeout(compileResult, gen)
                        : BuildResult.error(compileResult.formatDiagnostics());
            } catch (Throwable t) {
                result = BuildResult.error("Compiler failure: " + describe(t));
            }
            BuildResult finalResult = result;
            if (!stopped) {
                Platform.runLater(() -> apply(gen, finalResult));
            } else {
                finalResult.close();
            }
        });
    }

    private BuildResult buildWithTimeout(CompileResult result, long gen) {
        SnippetClassLoader cl = new SnippetClassLoader(
                result.getClassBytes(), getClass().getClassLoader());
        Future<Object> future = buildExecutor.submit(() -> {
            Class<?> klass = cl.loadClass(result.getEntryClassName());
            Method build = klass.getMethod("build");
            return build.invoke(null);
        });

        try {
            Object out = future.get((long) BUILD_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!(out instanceof Parent root)) {
                cl.close();
                return BuildResult.error("build() must return a javafx.scene.Parent (got "
                        + (out == null ? "null" : out.getClass().getName()) + ")");
            }
            return BuildResult.success(cl, root);
        } catch (TimeoutException e) {
            future.cancel(true);
            cl.close();
            return BuildResult.error("Runtime error in build(): timed out after "
                    + (long) BUILD_TIMEOUT.toSeconds() + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            cl.close();
            return BuildResult.error("Runtime error in build(): interrupted");
        } catch (ExecutionException e) {
            cl.close();
            return BuildResult.error("Runtime error in build(): " + describe(unwrap(e)));
        }
    }

    private void apply(long gen, BuildResult result) {
        if (stopped) {
            result.close();
            return;
        }
        if (gen != generation.get()) {
            result.close();
            return;
        }
        if (!result.isSuccess()) {
            onError.accept(result.error());
            return;
        }
        try {
            SnippetSession previous = current;
            SnippetSession next = new SnippetSession(result.classLoader(), result.root());
            onMount.accept(result.root());
            current = next;
            if (previous != null) {
                previous.close();
            }
        } catch (Throwable t) {
            result.close();
            onError.accept("Runtime error in build(): " + describe(t));
        }
    }

    public void shutdown() {
        stopped = true;
        debounce.stop();
        if (current != null) {
            current.close();
            current = null;
        }
        executor.shutdownNow();
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
