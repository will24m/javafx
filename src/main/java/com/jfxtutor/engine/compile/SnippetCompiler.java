package com.jfxtutor.engine.compile;

import com.jfxtutor.util.AppLog;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Wraps a user-provided snippet body in a generated class and compiles it
 * via {@link javax.tools.JavaCompiler}. The wrapper supplies a default set
 * of JavaFX imports so lessons can use {@code Label}, {@code VBox}, etc.
 * without typing imports.
 */
public class SnippetCompiler {

    public static final String GENERATED_PACKAGE = "_gen";
    public static final String GENERATED_CLASS = "_Snippet";
    public static final String ENTRY_FQCN = GENERATED_PACKAGE + "." + GENERATED_CLASS;

    private static final Pattern FORBIDDEN_PROCESS_EXIT = Pattern.compile(
            "\\bSystem\\s*\\.\\s*exit\\s*\\("
                    + "|\\bRuntime\\s*\\.\\s*getRuntime\\s*\\(\\s*\\)\\s*\\.\\s*(exit|halt)\\s*\\(");

    private static final String IMPORTS = String.join("\n",
            "import javafx.application.*;",
            "import javafx.beans.*;",
            "import javafx.beans.binding.*;",
            "import javafx.beans.property.*;",
            "import javafx.beans.value.*;",
            "import javafx.collections.*;",
            "import javafx.collections.transformation.*;",
            "import javafx.concurrent.*;",
            "import javafx.event.*;",
            "import javafx.scene.effect.*;",
            "import javafx.geometry.*;",
            "import javafx.scene.*;",
            "import javafx.scene.control.*;",
            "import javafx.scene.image.*;",
            "import javafx.scene.input.*;",
            "import javafx.scene.layout.*;",
            "import javafx.scene.media.*;",
            "import javafx.scene.paint.*;",
            "import javafx.scene.shape.*;",
            "import javafx.scene.text.*;",
            "import javafx.scene.web.*;",
            "import javafx.stage.*;",
            "import javafx.util.*;",
            "import java.util.*;",
            "import java.util.concurrent.*;",
            "import java.util.function.*;");

    private final JavaCompiler compiler;

    public SnippetCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException(
                    "No JavaCompiler is available. Are you running on a JDK (not a JRE)?");
        }
    }

    /** Compile the user's {@code build()} body into a fresh class. */
    public CompileResult compile(String userBody) {
        if (userBody == null) {
            userBody = "";
        }
        AppLog.info("compiler", "Preparing user snippet for in-memory compilation.");

        // Snippets run inside the same JVM as the tutor. Blocking System.exit()
        // protects the app from a lesson accidentally closing the whole process.
        if (FORBIDDEN_PROCESS_EXIT.matcher(userBody).find()) {
            AppLog.info("compiler", "Rejected snippet because it tried to exit the JVM.");
            return CompileResult.failureMessage(
                    "Process exit calls are disabled in snippets so the tutor can keep running.");
        }

        // The wrapper supplies package/imports/class/build() shape around the
        // learner's code so tiny lesson snippets can compile as complete Java.
        WrappedSource wrapped = wrap(userBody);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager std = compiler.getStandardFileManager(
                diagnostics, Locale.getDefault(), StandardCharsets.UTF_8);
             InMemoryJavaFileManager fm = new InMemoryJavaFileManager(std)) {

            InMemorySourceFile src = new InMemorySourceFile(ENTRY_FQCN, wrapped.source());
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null, fm, diagnostics, compilerOptions(), null, List.of(src));

            boolean ok = task.call();
            if (!ok) {
                AppLog.info("compiler", "Compilation produced "
                        + diagnostics.getDiagnostics().size() + " diagnostic(s).");
                return CompileResult.failure(
                        List.copyOf(diagnostics.getDiagnostics()), wrapped.userLineOffset());
            }
            AppLog.info("compiler", "Compilation succeeded; generated "
                    + fm.getOutputs().size() + " class file(s) in memory.");
            return CompileResult.success(fm.getOutputs(), ENTRY_FQCN);
        } catch (RuntimeException | IOException e) {
            AppLog.info("compiler", "Compiler infrastructure failed: " + e.getMessage());
            return CompileResult.failureMessage("Compiler failure: " + e.getMessage());
        }
    }

    /**
     * Build compiler flags so the snippet can see JavaFX classes.
     * At runtime the JavaFX plugin puts jars on the JVM module path
     * (jdk.module.path system property), not the classpath, so we must
     * forward both the module-path and an --add-modules directive.
     */
    private static List<String> compilerOptions() {
        List<String> opts = new java.util.ArrayList<>();
        opts.add("-Xlint:none");

        String modulePath = System.getProperty("jdk.module.path");
        if (modulePath != null && !modulePath.isBlank()) {
            // JavaFX lives on the module path when launched by the Gradle JavaFX
            // plugin, so the embedded compiler needs the same module visibility.
            opts.add("--module-path");
            opts.add(modulePath);
            opts.add("--add-modules");
            opts.add("javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.web,javafx.media");
        }

        // Also include the runtime classpath so any jars there are visible.
        String cp = System.getProperty("java.class.path");
        if (cp != null && !cp.isBlank()) {
            opts.add("-classpath");
            opts.add(cp);
        }

        return opts;
    }

    private static WrappedSource wrap(String userBody) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(GENERATED_PACKAGE).append(";\n");
        sb.append(IMPORTS).append("\n");
        sb.append("public class ").append(GENERATED_CLASS).append(" {\n");
        int generatedLinesBeforeUser = countLines(sb);
        if (userBodyHasBuild(userBody)) {
            // Advanced snippets may provide their own full build() method.
            sb.append(userBody).append("\n");
        } else {
            // Beginner snippets can be just the method body, for example:
            // return new StackPane(new Label("Hello"));
            sb.append("public static javafx.scene.Parent build() {\n");
            generatedLinesBeforeUser++;
            sb.append(userBody).append("\n");
            sb.append("}\n");
        }
        sb.append("}\n");
        return new WrappedSource(sb.toString(), generatedLinesBeforeUser);
    }

    private static boolean userBodyHasBuild(String body) {
        // This heuristic intentionally stays simple for lesson snippets: if the
        // learner wrote something that looks like Parent build(...), respect it.
        return body.contains("build(") && body.contains("Parent");
    }

    /** Diagnostic severity for callers that don't want full Diagnostic objects. */
    public static boolean isError(Diagnostic<?> d) {
        return d.getKind() == Diagnostic.Kind.ERROR;
    }

    private static int countLines(CharSequence text) {
        int lines = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }
        return lines;
    }

    private record WrappedSource(String source, int userLineOffset) {}
}
