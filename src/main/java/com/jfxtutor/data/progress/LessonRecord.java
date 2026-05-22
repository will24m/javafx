package com.jfxtutor.data.progress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Per-lesson progress record persisted to {@code state.json}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonRecord {

    /** When the lesson was first marked complete (all challenges passed). */
    public Instant completedAt;

    /** Total seconds the user spent with this lesson open across all sessions. */
    public long timeSpentSec;

    /** Maps challenge id → timestamp when that challenge was passed. */
    public Map<String, Instant> challengePasses = new HashMap<>();

    /** How many times the user clicked "Check" on any challenge in this lesson. */
    public int attemptsCount;

    /** Number of hints revealed. */
    public int hintsUsed;

    /** True if the user viewed the reference solution. */
    public boolean viewedSolution;

    public boolean isCompleted() { return completedAt != null; }
}
