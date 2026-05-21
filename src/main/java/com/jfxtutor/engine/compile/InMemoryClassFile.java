package com.jfxtutor.engine.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

/** A {@link javax.tools.JavaFileObject} that captures compiled bytecode in memory. */
public class InMemoryClassFile extends SimpleJavaFileObject {

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    public InMemoryClassFile(String binaryName) {
        // The URI only has to be unique and file-like enough for javac's APIs.
        // No file is created on disk.
        super(URI.create("mem:///" + binaryName.replace('.', '/') + ".class"), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() {
        // javac writes bytecode here as if it were writing to a .class file.
        return bytes;
    }

    public byte[] getBytes() {
        // Snapshot the compiled bytes for SnippetClassLoader#defineClass.
        return bytes.toByteArray();
    }
}
