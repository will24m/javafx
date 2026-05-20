package com.jfxtutor.engine.compile;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/** A {@link javax.tools.JavaFileObject} backed by an in-memory Java source string. */
public class InMemorySourceFile extends SimpleJavaFileObject {

    private final String source;

    public InMemorySourceFile(String binaryName, String source) {
        super(URI.create("mem:///" + binaryName.replace('.', '/') + ".java"), Kind.SOURCE);
        this.source = source;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return source;
    }
}
