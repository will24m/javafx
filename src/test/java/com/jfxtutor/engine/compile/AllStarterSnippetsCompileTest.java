package com.jfxtutor.engine.compile;

import com.jfxtutor.data.curriculum.CurriculumLoader;
import com.jfxtutor.data.curriculum.Lesson;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * One dynamic test per lesson — compiles the lesson's starter snippet through
 * the same {@link SnippetCompiler} the running IDE uses. Catches typos,
 * missing imports, and unresolved symbols in any lesson's frontmatter
 * before a user ever opens it.
 */
class AllStarterSnippetsCompileTest {

    private final SnippetCompiler compiler = new SnippetCompiler();

    @TestFactory
    Stream<DynamicTest> everyStarterSnippetCompiles() {
        List<Lesson> lessons = CurriculumLoader.loadAll();
        return lessons.stream().map(lesson -> dynamicTest(
                lesson.meta.id + " — " + lesson.meta.title,
                () -> {
                    String snippet = lesson.meta.starterSnippet == null
                            ? "" : lesson.meta.starterSnippet;
                    CompileResult result = compiler.compile(snippet);
                    assertTrue(result.isSuccess(),
                            () -> "Snippet failed to compile for lesson "
                                    + lesson.meta.id + ":\n"
                                    + result.formatDiagnostics());
                }));
    }
}
