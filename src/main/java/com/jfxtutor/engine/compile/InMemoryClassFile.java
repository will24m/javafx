package com.jfxtutor.engine.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

/** A {@link javax.tools.JavaFileObject} that captures compiled bytecode in memory. */
public class InMemoryClassFile extends SimpleJavaFileObject {

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    public InMemoryClassFile(String binaryName) {
        super(URI.create("mem:///" + binaryName.replace('.', '/') + ".class"), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() {
        return bytes;
    }

    public byte[] getBytes() {
        return bytes.toByteArray();
    }
}
