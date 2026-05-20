package com.jfxtutor.engine.compile;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

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
        String source = wrap(userBody);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager std = compiler.getStandardFileManager(
                diagnostics, Locale.getDefault(), StandardCharsets.UTF_8);
        InMemoryJavaFileManager fm = new InMemoryJavaFileManager(std);

        InMemorySourceFile src = new InMemorySourceFile(ENTRY_FQCN, source);
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fm, diagnostics, List.of("-Xlint:none"), null, List.of(src));

        boolean ok = task.call();
        if (!ok) {
            return CompileResult.failure(List.copyOf(diagnostics.getDiagnostics()));
        }
        return CompileResult.success(fm.getOutputs(), ENTRY_FQCN);
    }

    private static String wrap(String userBody) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(GENERATED_PACKAGE).append(";\n");
        sb.append(IMPORTS).append("\n");
        sb.append("public class ").append(GENERATED_CLASS).append(" {\n");
        if (userBodyHasBuild(userBody)) {
            sb.append(userBody).append("\n");
        } else {
            sb.append("public static javafx.scene.Parent build() {\n");
            sb.append(userBody).append("\n");
            sb.append("}\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static boolean userBodyHasBuild(String body) {
        return body.contains("build(") && body.contains("Parent");
    }

    /** Diagnostic severity for callers that don't want full Diagnostic objects. */
    public static boolean isError(Diagnostic<?> d) {
        return d.getKind() == Diagnostic.Kind.ERROR;
    }
}
