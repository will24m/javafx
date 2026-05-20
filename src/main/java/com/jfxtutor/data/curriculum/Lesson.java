package com.jfxtutor.data.curriculum;

public class Lesson {
    public final LessonFrontmatter meta;
    /** Raw Markdown body (everything after the closing --- frontmatter delimiter). */
    public final String markdownBody;

    public Lesson(LessonFrontmatter meta, String markdownBody) {
        this.meta = meta;
        this.markdownBody = markdownBody;
    }

    @Override
    public String toString() {
        return meta.title;
    }
}
