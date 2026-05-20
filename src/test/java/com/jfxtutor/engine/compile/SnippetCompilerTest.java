package com.jfxtutor.engine.compile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnippetCompilerTest {

    private final SnippetCompiler compiler = new SnippetCompiler();

    @Test
    void compilesSnippetBodyIntoEntryClass() {
        CompileResult result = compiler.compile("""
                Label label = new Label("Hi");
                return new StackPane(label);
                """);

        assertTrue(result.isSuccess(), result::formatDiagnostics);
        assertTrue(result.getClassBytes().containsKey(SnippetCompiler.ENTRY_FQCN));
    }

    @Test
    void mapsCompilerDiagnosticsBackToUserLineNumbers() {
        CompileResult result = compiler.compile("""
                Label label = new Label("Hi");
                return missingSymbol;
                """);

        assertFalse(result.isSuccess());
        assertTrue(result.formatDiagnostics().contains("line 2"), result::formatDiagnostics);
    }

    @Test
    void rejectsProcessExitCallsBeforeCompilation() {
        CompileResult result = compiler.compile("""
                System.exit(0);
                return new StackPane();
                """);

        assertFalse(result.isSuccess());
        assertTrue(result.formatDiagnostics().contains("disabled"));
    }
}
