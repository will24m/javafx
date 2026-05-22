package com.jfxtutor.data.progress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Root progress document written to {@code ~/.javafx-tutor/state.json}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Progress {

    /** Schema version — increment when the format changes incompatibly. */
    public int version = 1;

    /** The lesson id last open when the app closed. */
    public String lastLessonId;

    /** Per-lesson records keyed by lesson id. */
    public Map<String, LessonRecord> completed = new HashMap<>();

    /** Persisted user preferences (theme, fontSize, etc.). */
    public Map<String, String> preferences = new HashMap<>();

    /** Streak: how many consecutive calendar days the user completed ≥1 lesson. */
    public int streakDays;

    /** Total seconds the app has been open and a lesson selected, across all sessions. */
    public long totalSecondsActive;
}
