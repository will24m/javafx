package com.jfxtutor.engine.compile;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.HashMap;
import java.util.Map;

/** Forwards file lookups to the host file manager but stores outputs in memory. */
public class InMemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, InMemoryClassFile> outputs = new HashMap<>();

    public InMemoryJavaFileManager(JavaFileManager delegate) {
        super(delegate);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
                                               String className,
                                               JavaFileObject.Kind kind,
                                               FileObject sibling) {
        InMemoryClassFile file = new InMemoryClassFile(className);
        outputs.put(className, file);
        return file;
    }

    public Map<String, byte[]> getOutputs() {
        Map<String, byte[]> result = new HashMap<>();
        outputs.forEach((name, file) -> result.put(name, file.getBytes()));
        return result;
    }
}
