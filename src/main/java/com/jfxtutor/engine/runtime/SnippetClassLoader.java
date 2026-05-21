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
        // Give each loader a readable name for debuggers/thread dumps. The
        // identity hash keeps names distinct across snippet generations.
        super("snippet-" + System.identityHashCode(classBytes), parent);
        // Copy the map so later callers cannot mutate the bytecode set while
        // this loader is resolving classes.
        this.classBytes = new HashMap<>(classBytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // Parent delegation already tried the normal application/JDK/JavaFX
        // classes. If the name is in this map, it is generated snippet bytecode.
        byte[] bytes = classBytes.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public void close() {
        // ClassLoader has no hard close hook for defined classes, but dropping
        // the byte arrays releases the largest data we hold directly.
        classBytes.clear();
    }
}
