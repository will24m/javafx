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
}
