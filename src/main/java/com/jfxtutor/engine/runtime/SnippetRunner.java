package com.jfxtutor.engine.runtime;

import com.jfxtutor.engine.compile.CompileResult;
import com.jfxtutor.engine.compile.SnippetCompiler;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final SnippetCompiler compiler = new SnippetCompiler();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "snippet-compile");
        t.setDaemon(true);
        return t;
    });

    private final Consumer<Parent> onMount;
    private final Consumer<String> onError;
    private final PauseTransition debounce;

    private final AtomicLong generation = new AtomicLong();
    private SnippetSession current;
    private String pendingSource;

    public SnippetRunner(Consumer<Parent> onMount, Consumer<String> onError) {
        this.onMount = onMount;
        this.onError = onError;
        this.debounce = new PauseTransition(DEBOUNCE);
        this.debounce.setOnFinished(e -> fire());
    }

    /** Editor calls this on every keystroke. */
    public void scheduleRecompile(String source) {
        this.pendingSource = source;
        debounce.playFromStart();
    }

    /** Skip debounce — useful for the initial mount after lesson load. */
    public void recompileNow(String source) {
        this.pendingSource = source;
        debounce.stop();
        fire();
    }

    private void fire() {
        String source = pendingSource;
        if (source == null) return;
        long gen = generation.incrementAndGet();
        executor.submit(() -> {
            CompileResult result = compiler.compile(source);
            Platform.runLater(() -> apply(gen, result));
        });
    }

    private void apply(long gen, CompileResult result) {
        if (gen != generation.get()) {
            // A newer compile has been scheduled; this result is stale.
            return;
        }
        if (!result.isSuccess()) {
            onError.accept(result.formatDiagnostics());
            return;
        }
        try {
            SnippetClassLoader cl = new SnippetClassLoader(
                    result.getClassBytes(), getClass().getClassLoader());
            Class<?> klass = cl.loadClass(result.getEntryClassName());
            Method build = klass.getMethod("build");
            Object out = build.invoke(null);
            if (!(out instanceof Parent root)) {
                onError.accept("build() must return a javafx.scene.Parent (got "
                        + (out == null ? "null" : out.getClass().getName()) + ")");
                cl.close();
                return;
            }
            SnippetSession previous = current;
            current = new SnippetSession(cl, root);
            onMount.accept(root);
            if (previous != null) {
                Platform.runLater(previous::close);
            }
        } catch (Throwable t) {
            Throwable cause = t.getCause() != null ? t.getCause() : t;
            onError.accept("Runtime error in build(): " + cause.getClass().getSimpleName()
                    + ": " + cause.getMessage());
        }
    }

    public void shutdown() {
        debounce.stop();
        if (current != null) {
            current.close();
            current = null;
        }
        executor.shutdownNow();
    }
}
