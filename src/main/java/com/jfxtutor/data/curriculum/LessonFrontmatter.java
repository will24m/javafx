package com.jfxtutor.data.curriculum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * YAML frontmatter schema for a lesson Markdown file.
 *
 * Fields are public on purpose: Jackson can hydrate the object without setters,
 * and the rest of the app reads this object as simple structured data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonFrontmatter {
    /** Stable lesson id, usually matching the Markdown filename prefix. */
    public String id;
    /** Curriculum group shown as a collapsible tier in the navigator. */
    public String tier;
    /** Global display order across every tier. */
    public int order;
    /** Human-readable lesson title. */
    public String title;
    /** Learning goals listed in the lesson metadata. */
    public List<String> objectives;
    /** Rough time estimate shown in the app header/status. */
    public int estimatedMinutes;
    /** Java code inserted into the editor when the lesson opens. */
    public String starterSnippet;
    /** Optional practice checks associated with the lesson. */
    public List<ChallengeDef> challenges;
    /** Optional id/path pointer for lesson sequencing. */
    public String nextLesson;
}
