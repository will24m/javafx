package com.jfxtutor.data.progress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jfxtutor.util.AppLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

/**
 * Reads and writes the user's progress document at
 * {@code ~/.javafx-tutor/state.json}.
 *
 * <p>Writes are atomic: a temp file is written first, then moved over the target
 * so a crash during write never leaves a corrupt state file.
 *
 * <p>This class is not thread-safe. All calls must happen on the JavaFX Application
 * Thread, same as the rest of the app's data flow.
 */
public final class ProgressStore {

    private static final String APP_DIR_NAME = ".javafx-tutor";
    private static final String STATE_FILE    = "state.json";
    private static final String BACKUP_SUFFIX = ".v%d.bak";

    private final Path baseDir;
    private final Path stateFile;
    private final ObjectMapper mapper;

    private Progress current;
    private final SnippetStore snippetStore;

    public ProgressStore() {
        this.baseDir   = Paths.get(System.getProperty("user.home"), APP_DIR_NAME);
        this.stateFile = baseDir.resolve(STATE_FILE);
        this.mapper    = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
        this.snippetStore = new SnippetStore(baseDir);
    }

    // ── lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Loads progress from disk. Must be called once at startup before any other
     * method. Returns this store for chaining.
     */
    public ProgressStore load() {
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            AppLog.info("progress", "Could not create app dir: " + e.getMessage());
        }
        if (!Files.exists(stateFile)) {
            AppLog.info("progress", "No state file found; starting fresh.");
            current = new Progress();
            return this;
        }
        try {
            Progress loaded = mapper.readValue(stateFile.toFile(), Progress.class);
            if (loaded.version > ProgressMigrator.CURRENT_VERSION) {
                backupAndReset(loaded.version);
            } else {
                current = ProgressMigrator.migrate(loaded);
                AppLog.info("progress", "Loaded progress: "
                        + current.completed.size() + " lessons completed, "
                        + "last lesson=" + current.lastLessonId + ".");
            }
        } catch (IOException e) {
            AppLog.info("progress",
                    "Could not parse state.json; starting fresh. Error: " + e.getMessage());
            current = new Progress();
        }
        return this;
    }

    /** Flushes the current progress to disk. Safe to call frequently. */
    public void flush() {
        if (current == null) return;
        try {
            Files.createDirectories(baseDir);
            Path tmp = baseDir.resolve(STATE_FILE + ".tmp");
            mapper.writeValue(tmp.toFile(), current);
            Files.move(tmp, stateFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            AppLog.info("progress", "Could not flush progress: " + e.getMessage());
        }
    }

    // ── lesson events ──────────────────────────────────────────────────────────

    /** Record that the user opened {@code lessonId}. */
    public void setLastLesson(String lessonId) {
        ensure();
        current.lastLessonId = lessonId;
    }

    /** @return the last-opened lesson id, or {@code null} if none. */
    public String getLastLessonId() {
        ensure();
        return current.lastLessonId;
    }

    /**
     * Record that challenge {@code challengeId} in lesson {@code lessonId} was passed.
     * Marks the lesson as completed when all listed challenge ids have been passed.
     */
    public void recordChallengePass(String lessonId, String challengeId) {
        ensure();
        LessonRecord rec = current.completed.computeIfAbsent(lessonId, k -> new LessonRecord());
        rec.challengePasses.put(challengeId, Instant.now());
        rec.attemptsCount++;
        AppLog.info("progress",
                "Challenge pass recorded: lesson=" + lessonId + ", challenge=" + challengeId);
    }

    /** Mark a challenge attempt (regardless of pass/fail). */
    public void recordAttempt(String lessonId) {
        ensure();
        current.completed.computeIfAbsent(lessonId, k -> new LessonRecord()).attemptsCount++;
    }

    /** Mark lesson {@code lessonId} fully completed. */
    public void markLessonCompleted(String lessonId) {
        ensure();
        LessonRecord rec = current.completed.computeIfAbsent(lessonId, k -> new LessonRecord());
        if (rec.completedAt == null) {
            rec.completedAt = Instant.now();
            AppLog.info("progress", "Lesson completed: " + lessonId);
        }
    }

    /** Returns true if all declared challenge ids for a lesson have been passed. */
    public boolean isChallengeComplete(String lessonId, String challengeId) {
        ensure();
        LessonRecord rec = current.completed.get(lessonId);
        return rec != null && rec.challengePasses.containsKey(challengeId);
    }

    public boolean isLessonCompleted(String lessonId) {
        ensure();
        LessonRecord rec = current.completed.get(lessonId);
        return rec != null && rec.isCompleted();
    }

    public LessonRecord getRecord(String lessonId) {
        ensure();
        return current.completed.get(lessonId);
    }

    // ── snippet passthrough ───────────────────────────────────────────────────

    public void saveSnippet(String lessonId, String source) {
        snippetStore.save(lessonId, source);
    }

    /** Returns the user's last-edited snippet for this lesson, or {@code null}. */
    public String loadSnippet(String lessonId) {
        return snippetStore.load(lessonId);
    }

    // ── preferences ───────────────────────────────────────────────────────────

    public String getPreference(String key, String defaultValue) {
        ensure();
        return current.preferences.getOrDefault(key, defaultValue);
    }

    public void setPreference(String key, String value) {
        ensure();
        current.preferences.put(key, value);
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private void ensure() {
        if (current == null) {
            AppLog.info("progress", "ProgressStore.load() was not called; loading now.");
            load();
        }
    }

    private void backupAndReset(int foundVersion) {
        String backupName = STATE_FILE + String.format(BACKUP_SUFFIX, foundVersion);
        Path backup = baseDir.resolve(backupName);
        try {
            Files.copy(stateFile, backup, StandardCopyOption.REPLACE_EXISTING);
            AppLog.info("progress",
                    "State file has future version " + foundVersion
                            + "; backed up to " + backupName + " and starting fresh.");
        } catch (IOException e) {
            AppLog.info("progress", "Could not back up state file: " + e.getMessage());
        }
        current = new Progress();
    }
}
