package com.jfxtutor.engine.challenge;

import com.jfxtutor.data.curriculum.CurriculumLoader;
import com.jfxtutor.data.curriculum.Lesson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that every {@code assertion:} string across all 100 lessons can be
 * parsed by {@link AssertionParser} without throwing.
 *
 * A parse failure here means either the lesson frontmatter has a typo or a new
 * DSL function is referenced before it is implemented.
 */
class ChallengeAssertionsCoverageTest {

    @Test
    void allLessonAssertionsParse() {
        List<Lesson> lessons = CurriculumLoader.loadAll();
        List<String> failures = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.meta.challenges == null) continue;
            for (var challenge : lesson.meta.challenges) {
                if (challenge.assertion == null || challenge.assertion.isBlank()) continue;
                try {
                    AssertionParser.parse(challenge.assertion);
                } catch (IllegalArgumentException e) {
                    failures.add(lesson.meta.id + " / " + challenge.id
                            + " → " + e.getMessage());
                }
            }
        }

        assertTrue(failures.isEmpty(),
                "The following assertion strings failed to parse:\n"
                        + String.join("\n", failures));
    }
}
