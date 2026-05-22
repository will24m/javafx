package com.jfxtutor.engine.challenge;

import javafx.scene.Parent;

/**
 * A single evaluatable challenge condition.
 *
 * Implementations are produced by {@link Assertions} from a parsed DSL string,
 * or written as Java classes under {@code challenges/} for complex lessons.
 */
@FunctionalInterface
public interface ChallengeAssertion {
    AssertionResult check(Parent snippetRoot);
}
