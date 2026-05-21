package com.jfxtutor.data.curriculum;

/**
 * Immutable lesson model consumed by the UI.
 *
 * The metadata comes from YAML frontmatter, and markdownBody is the raw lesson
 * prose after the frontmatter delimiter. Keeping both parts together makes
 * LessonNavigator and LessonPane share the same source of truth.
 */
public class Lesson {
    public final LessonFrontmatter meta;
    /** Raw Markdown body (everything after the closing --- frontmatter delimiter). */
    public final String markdownBody;

    public Lesson(LessonFrontmatter meta, String markdownBody) {
        // No defensive copying is needed here because LessonFrontmatter is
        // created by Jackson for one lesson file and then treated as read-only.
        this.meta = meta;
        this.markdownBody = markdownBody;
    }

    @Override
    public String toString() {
        return meta.title;
    }
}
