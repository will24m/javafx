package com.jfxtutor.data.progress;

import com.jfxtutor.util.AppLog;

/**
 * Migrates a {@link Progress} object read from disk to the current schema version.
 *
 * <p>Each migration step is idempotent. If the version on disk is already current,
 * this is a no-op.
 */
public final class ProgressMigrator {

    static final int CURRENT_VERSION = 1;

    private ProgressMigrator() {}

    /**
     * Returns a {@link Progress} at {@link #CURRENT_VERSION}, migrating in-place
     * if needed.
     */
    public static Progress migrate(Progress p) {
        if (p.version >= CURRENT_VERSION) return p;
        AppLog.info("progress", "Migrating progress from version " + p.version
                + " to " + CURRENT_VERSION + ".");
        // v0 → v1: no structural changes yet; just bump the version.
        p.version = CURRENT_VERSION;
        return p;
    }
}
