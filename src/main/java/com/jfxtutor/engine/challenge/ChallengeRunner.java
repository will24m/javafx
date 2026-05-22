package com.jfxtutor.engine.challenge;

import com.jfxtutor.data.curriculum.ChallengeDef;
import com.jfxtutor.util.AppLog;
import javafx.scene.Parent;

/**
 * Evaluates a {@link ChallengeDef} against the current snippet preview root.
 *
 * This is the boundary between the data model (frontmatter DSL strings) and the
 * live scene graph. It parses the assertion string once per evaluation, so
 * author-side parse errors surface when the user clicks "Check" rather than at
 * startup.
 */
public final class ChallengeRunner {

    private ChallengeRunner() {}

    /**
     * Parses {@code def.assertion} and evaluates it against {@code snippetRoot}.
     *
     * @param def         challenge definition from lesson frontmatter
     * @param snippetRoot current preview root (may be null if compile failed)
     * @return evaluation result — never throws
     */
    public static AssertionResult run(ChallengeDef def, Parent snippetRoot) {
        if (def == null) {
            return AssertionResult.fail("No challenge definition provided.");
        }
        if (snippetRoot == null) {
            return AssertionResult.fail(
                    "The preview is not showing a compiled snippet yet. "
                            + "Fix any compilation errors, then check the challenge.");
        }
        if (def.assertion == null || def.assertion.isBlank()) {
            return AssertionResult.fail(
                    "This challenge has no assertion defined yet.");
        }

        ChallengeAssertion assertion;
        try {
            assertion = AssertionParser.parse(def.assertion);
        } catch (IllegalArgumentException e) {
            AppLog.info("challenge",
                    "Parse error in assertion for challenge " + def.id + ": " + e.getMessage());
            return AssertionResult.fail(
                    "Lesson author error — could not parse assertion: " + e.getMessage());
        }

        try {
            AssertionResult result = assertion.check(snippetRoot);
            AppLog.info("challenge",
                    "Challenge " + def.id + " evaluated: " + (result.passed() ? "PASS" : "FAIL")
                            + " — " + result.message());
            return result;
        } catch (Throwable t) {
            AppLog.info("challenge",
                    "Assertion threw during evaluation of " + def.id + ": " + t);
            return AssertionResult.fail(
                    "Assertion evaluation error: " + t.getClass().getSimpleName()
                            + (t.getMessage() != null ? ": " + t.getMessage() : ""));
        }
    }
}
