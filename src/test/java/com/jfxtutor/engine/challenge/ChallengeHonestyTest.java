package com.jfxtutor.engine.challenge;

import com.jfxtutor.data.curriculum.ChallengeDef;
import com.jfxtutor.data.curriculum.CurriculumLoader;
import com.jfxtutor.data.curriculum.Lesson;
import com.jfxtutor.engine.compile.CompileResult;
import com.jfxtutor.engine.compile.SnippetCompiler;
import com.jfxtutor.engine.runtime.SnippetClassLoader;
import javafx.application.Platform;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the "honesty contract" for every challenge that declares
 * a {@code starterSnippet} or {@code solutionSnippet}:
 *
 * <ul>
 *   <li>{@code starterSnippet} must FAIL the assertion — the learner has real work to do.</li>
 *   <li>{@code solutionSnippet} must PASS the assertion — the assertion accepts a correct answer.</li>
 * </ul>
 *
 * A challenge that omits both fields is skipped; it is not required to declare them.
 */
class ChallengeHonestyTest {

    private static final SnippetCompiler COMPILER = new SnippetCompiler();

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    void starterSnippetFailsAndSolutionPasses() throws Exception {
        List<Lesson> lessons = CurriculumLoader.loadAll();
        List<String> failures = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.meta.challenges == null) continue;
            for (ChallengeDef def : lesson.meta.challenges) {
                if (def.assertion == null || def.assertion.isBlank()) continue;

                ChallengeAssertion assertion;
                try {
                    assertion = AssertionParser.parse(def.assertion);
                } catch (IllegalArgumentException e) {
                    // Already caught by ChallengeAssertionsCoverageTest — skip here.
                    continue;
                }

                // Starter must FAIL
                if (def.starterSnippet != null && !def.starterSnippet.isBlank()) {
                    Parent root = compileAndBuild(def.starterSnippet);
                    if (root != null) {
                        AssertionResult r = assertion.check(root);
                        if (r.passed()) {
                            failures.add(lesson.meta.id + " / " + def.id
                                    + ": starterSnippet already PASSES the assertion — "
                                    + "learner has nothing to do. Assertion: " + def.assertion);
                        }
                    }
                }

                // Solution must PASS
                if (def.solutionSnippet != null && !def.solutionSnippet.isBlank()) {
                    Parent root = compileAndBuild(def.solutionSnippet);
                    if (root == null) {
                        failures.add(lesson.meta.id + " / " + def.id
                                + ": solutionSnippet failed to compile or build.");
                    } else {
                        AssertionResult r = assertion.check(root);
                        if (!r.passed()) {
                            failures.add(lesson.meta.id + " / " + def.id
                                    + ": solutionSnippet FAILS the assertion — "
                                    + "the assertion rejects a valid solution. "
                                    + "Message: " + r.message());
                        }
                    }
                }
            }
        }

        assertTrue(failures.isEmpty(),
                "Challenge honesty failures:\n" + String.join("\n", failures));
    }

    private static Parent compileAndBuild(String snippetBody) {
        try {
            CompileResult result = COMPILER.compile(snippetBody);
            if (!result.isSuccess()) return null;
            SnippetClassLoader loader = new SnippetClassLoader(
                    result.getClassBytes(), ChallengeHonestyTest.class.getClassLoader());
            Class<?> cls = loader.loadClass("_gen._Snippet");
            Method build = cls.getMethod("build");
            return (Parent) build.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }
}
