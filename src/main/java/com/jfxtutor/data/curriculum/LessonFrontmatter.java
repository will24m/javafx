package com.jfxtutor.data.curriculum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonFrontmatter {
    public String id;
    public String tier;
    public int order;
    public String title;
    public List<String> objectives;
    public int estimatedMinutes;
    public String starterSnippet;
    public List<ChallengeDef> challenges;
    public String nextLesson;
}
