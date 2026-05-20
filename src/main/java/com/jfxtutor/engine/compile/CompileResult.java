package com.jfxtutor.engine.compile;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Map;

/** Result of a snippet compile: either bytecode + entry class, or a list of diagnostics. */
public final class CompileResult {

    private final boolean success;
    private final Map<String, byte[]> classBytes;
    private final String entryClassName;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;
    private final int generatedLineOffset;
    private final String message;

    private CompileResult(boolean success,
                          Map<String, byte[]> classBytes,
                          String entryClassName,
                          List<Diagnostic<? extends JavaFileObject>> diagnostics,
                          int generatedLineOffset,
                          String message) {
        this.success = success;
        this.classBytes = classBytes;
        this.entryClassName = entryClassName;
        this.diagnostics = diagnostics;
        this.generatedLineOffset = generatedLineOffset;
        this.message = message;
    }

    public static CompileResult success(Map<String, byte[]> classBytes, String entryClassName) {
        return new CompileResult(true, classBytes, entryClassName, List.of(), 0, null);
    }

    public static CompileResult failure(List<Diagnostic<? extends JavaFileObject>> diagnostics,
                                        int generatedLineOffset) {
        return new CompileResult(false, Map.of(), null, diagnostics, generatedLineOffset, null);
    }

    public static CompileResult failureMessage(String message) {
        return new CompileResult(false, Map.of(), null, List.of(), 0, message);
    }

    public boolean isSuccess() { return success; }
    public Map<String, byte[]> getClassBytes() { return classBytes; }
    public String getEntryClassName() { return entryClassName; }
    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() { return diagnostics; }

    /** Human-readable single-string rendering of all diagnostics. */
    public String formatDiagnostics() {
        if (message != null && !message.isBlank()) {
            return message;
        }
        StringBuilder sb = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics) {
            long userLine = mapGeneratedLine(d.getLineNumber());
            sb.append(d.getKind()).append(" at line ").append(userLine)
              .append(": ").append(d.getMessage(null)).append("\n");
        }
        return sb.toString().trim();
    }

    private long mapGeneratedLine(long generatedLine) {
        if (generatedLine <= 0 || generatedLineOffset <= 0) {
            return generatedLine;
        }
        return Math.max(1, generatedLine - generatedLineOffset);
    }
}
