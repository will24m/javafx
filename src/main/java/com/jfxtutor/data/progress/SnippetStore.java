package com.jfxtutor.data.progress;

import com.jfxtutor.util.AppLog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Saves and loads the last-edited snippet for each lesson.
 *
 * <p>Snippets are stored as plain {@code .java} files under
 * {@code ~/.javafx-tutor/snippets/<lessonId>.java} — raw text, easy to grep.
 */
public final class SnippetStore {

    private final Path snippetsDir;

    public SnippetStore(Path baseDir) {
        this.snippetsDir = baseDir.resolve("snippets");
    }

    /** Returns the last saved snippet for {@code lessonId}, or {@code null} if none. */
    public String load(String lessonId) {
        Path file = snippetsDir.resolve(sanitize(lessonId) + ".java");
        if (!Files.exists(file)) return null;
        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            AppLog.info("progress", "Loaded saved snippet for " + lessonId + ".");
            return text;
        } catch (IOException e) {
            AppLog.info("progress", "Could not read snippet for " + lessonId + ": " + e.getMessage());
            return null;
        }
    }

    /** Persists {@code source} for {@code lessonId}. Writes atomically via a temp file. */
    public void save(String lessonId, String source) {
        if (source == null) return;
        try {
            Files.createDirectories(snippetsDir);
            Path target = snippetsDir.resolve(sanitize(lessonId) + ".java");
            Path tmp = snippetsDir.resolve(sanitize(lessonId) + ".java.tmp");
            Files.writeString(tmp, source, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmp, target,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            AppLog.info("progress", "Could not save snippet for " + lessonId + ": " + e.getMessage());
        }
    }

    /** Strip path traversal characters so lessonIds are safe as filenames. */
    private static String sanitize(String id) {
        return id.replaceAll("[^A-Za-z0-9_\\-]", "_");
    }
}
