package com.jfxtutor.data.curriculum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jfxtutor.util.AppLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads Markdown lesson files from the classpath and turns them into Lesson objects.
 *
 * Each lesson file is split into:
 * - YAML frontmatter between the opening and closing --- markers,
 * - Markdown body after the closing marker.
 *
 * The frontmatter drives navigation and starter code. The body is rendered by
 * LessonPane as rich JavaFX text.
 */
public class CurriculumLoader {

    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    /**
     * Loads all lessons listed in /curriculum/index.txt from the classpath.
     * Returns them sorted by global {@code order} (which is also unique
     * within each tier), so the navigator opens to lesson 001 regardless of
     * how tier names sort alphabetically.
     */
    public static List<Lesson> loadAll() {
        AppLog.info("curriculum", "Reading curriculum/index.txt to discover lesson files.");
        List<String> paths = readIndex();
        AppLog.info("curriculum", "Index contains " + paths.size() + " lesson paths.");
        List<Lesson> lessons = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        for (String path : paths) {
            try {
                AppLog.info("curriculum", "Loading lesson resource: " + path);
                lessons.add(loadLesson(path));
            } catch (Exception e) {
                failures.add(path + ": " + e.getMessage());
            }
        }
        if (!failures.isEmpty()) {
            AppLog.info("curriculum", "Curriculum loading failed for " + failures.size() + " lesson(s).");
            throw new IllegalStateException("Failed to load curriculum:\n"
                    + String.join("\n", failures));
        }
        lessons.sort((a, b) -> Integer.compare(a.meta.order, b.meta.order));
        AppLog.info("curriculum", "Loaded and sorted " + lessons.size() + " lessons by frontmatter order.");
        return lessons;
    }

    private static List<String> readIndex() {
        // The Gradle application plugin puts src/main/resources on the runtime
        // classpath, so classloader resource lookup works both in development
        // and after packaging.
        try (InputStream is = CurriculumLoader.class.getClassLoader()
                .getResourceAsStream("curriculum/index.txt")) {
            if (is == null) throw new IllegalStateException("curriculum/index.txt not found on classpath");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .filter(l -> !l.isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read curriculum index", e);
        }
    }

    static Lesson loadLesson(String classpathPath) throws IOException {
        // Lesson paths in index.txt are classpath-relative, for example:
        // curriculum/foundations/001-what-is-a-stage.md
        try (InputStream is = CurriculumLoader.class.getClassLoader()
                .getResourceAsStream(classpathPath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + classpathPath);
            String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return parse(raw);
        }
    }

    static Lesson parse(String raw) throws IOException {
        // A missing or malformed frontmatter block usually means the Markdown
        // file is not following the curriculum contract.
        if (!raw.startsWith("---")) throw new IllegalArgumentException("Missing frontmatter");
        int end = raw.indexOf("\n---", 3);
        if (end < 0) throw new IllegalArgumentException("Unclosed frontmatter");
        String yaml = raw.substring(3, end).trim();
        // Body starts after the closing ---\n
        String body = raw.substring(end + 4).trim();
        LessonFrontmatter meta = YAML.readValue(yaml, LessonFrontmatter.class);
        validate(meta);
        return new Lesson(meta, body);
    }

    private static void validate(LessonFrontmatter meta) {
        // Fail fast here so the UI never has to guess how to render a lesson
        // without an id, title, tier, or stable ordering number.
        if (meta == null) {
            throw new IllegalArgumentException("Missing frontmatter values");
        }
        if (isBlank(meta.id)) {
            throw new IllegalArgumentException("Missing required frontmatter field: id");
        }
        if (isBlank(meta.tier)) {
            throw new IllegalArgumentException("Missing required frontmatter field: tier");
        }
        if (meta.order <= 0) {
            throw new IllegalArgumentException("Invalid required frontmatter field: order");
        }
        if (isBlank(meta.title)) {
            throw new IllegalArgumentException("Missing required frontmatter field: title");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
