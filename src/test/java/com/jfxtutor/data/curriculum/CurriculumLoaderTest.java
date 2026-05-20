package com.jfxtutor.data.curriculum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurriculumLoaderTest {

    @Test
    void parsesValidLessonFrontmatterAndBody() throws Exception {
        Lesson lesson = CurriculumLoader.parse("""
                ---
                id: test-lesson
                tier: foundations
                order: 1
                title: "Test Lesson"
                ---

                # Test Lesson

                Body text.
                """);

        assertEquals("test-lesson", lesson.meta.id);
        assertEquals("Body text.", lesson.markdownBody.lines().skip(2).findFirst().orElse(""));
    }

    @Test
    void rejectsMissingRequiredFrontmatterFields() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                CurriculumLoader.parse("""
                        ---
                        id: test-lesson
                        tier: foundations
                        order: 1
                        ---

                        # Missing title
                        """));

        assertEquals("Missing required frontmatter field: title", thrown.getMessage());
    }
}
