package com.jfxtutor.engine.compile;

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
            "import javafx.beans.property.*;",
            "import javafx.beans.value.*;",
            "import javafx.collections.*;",
            "import javafx.event.*;",
            "import javafx.geometry.*;",
            "import javafx.scene.*;",
            "import javafx.scene.control.*;",
            "import javafx.scene.input.*;",
            "import javafx.scene.layout.*;",
            "import javafx.scene.paint.*;",
            "import javafx.scene.shape.*;",
            "import javafx.scene.text.*;",
            "import java.util.*;",
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
        if (FORBIDDEN_PROCESS_EXIT.matcher(userBody).find()) {
            return CompileResult.failureMessage(
                    "Process exit calls are disabled in snippets so the tutor can keep running.");
        }

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
                return CompileResult.failure(
                        List.copyOf(diagnostics.getDiagnostics()), wrapped.userLineOffset());
            }
            return CompileResult.success(fm.getOutputs(), ENTRY_FQCN);
        } catch (RuntimeException | IOException e) {
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
            opts.add("--module-path");
            opts.add(modulePath);
            opts.add("--add-modules");
            opts.add("javafx.controls,javafx.fxml,javafx.graphics,javafx.base");
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
            sb.append(userBody).append("\n");
        } else {
            sb.append("public static javafx.scene.Parent build() {\n");
            generatedLinesBeforeUser++;
            sb.append(userBody).append("\n");
            sb.append("}\n");
        }
        sb.append("}\n");
        return new WrappedSource(sb.toString(), generatedLinesBeforeUser);
    }

    private static boolean userBodyHasBuild(String body) {
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
