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

    private CompileResult(boolean success,
                          Map<String, byte[]> classBytes,
                          String entryClassName,
                          List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        this.success = success;
        this.classBytes = classBytes;
        this.entryClassName = entryClassName;
        this.diagnostics = diagnostics;
    }

    public static CompileResult success(Map<String, byte[]> classBytes, String entryClassName) {
        return new CompileResult(true, classBytes, entryClassName, List.of());
    }

    public static CompileResult failure(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        return new CompileResult(false, Map.of(), null, diagnostics);
    }

    public boolean isSuccess() { return success; }
    public Map<String, byte[]> getClassBytes() { return classBytes; }
    public String getEntryClassName() { return entryClassName; }
    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() { return diagnostics; }

    /** Human-readable single-string rendering of all diagnostics. */
    public String formatDiagnostics() {
        StringBuilder sb = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics) {
            sb.append(d.getKind()).append(" at line ").append(d.getLineNumber())
              .append(": ").append(d.getMessage(null)).append("\n");
        }
        return sb.toString().trim();
    }
}
