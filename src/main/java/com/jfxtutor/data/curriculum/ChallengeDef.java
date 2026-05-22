package com.jfxtutor.data.curriculum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Frontmatter definition for an optional lesson challenge.
 *
 * Challenge execution is intentionally not implemented here; this class just
 * preserves the structured challenge data parsed from Markdown.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeDef {
    /** Stable challenge id inside a lesson. */
    public String id;
    /** Learner-facing challenge prompt. */
    public String description;
    /** Raw assertion string, e.g. containsLabeledInside(text="Submit", parentType=VBox) */
    public String assertion;
    /**
     * Optional snippet that must FAIL this assertion — used by tests to verify the
     * challenge is not trivially passable without learner effort. Typically the lesson's
     * own starterSnippet body. If null the honesty test is skipped.
     */
    public String starterSnippet;
    /**
     * Optional snippet that must PASS this assertion — used by tests to verify the
     * assertion actually accepts a correct solution. If null the solution test is skipped.
     */
    public String solutionSnippet;
}
