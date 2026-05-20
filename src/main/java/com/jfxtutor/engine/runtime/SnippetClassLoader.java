package com.jfxtutor.engine.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * URL-less ClassLoader that defines classes from an in-memory byte map produced
 * by {@link com.jfxtutor.engine.compile.SnippetCompiler}. Delegates to its parent
 * for everything outside the in-memory set so JavaFX and JDK classes resolve
 * normally.
 */
public class SnippetClassLoader extends ClassLoader implements AutoCloseable {

    private final Map<String, byte[]> classBytes;

    public SnippetClassLoader(Map<String, byte[]> classBytes, ClassLoader parent) {
        super("snippet-" + System.identityHashCode(classBytes), parent);
        this.classBytes = new HashMap<>(classBytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public void close() {
        classBytes.clear();
    }
}
