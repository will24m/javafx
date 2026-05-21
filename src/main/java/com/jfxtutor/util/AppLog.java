package com.jfxtutor.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Small console logger used by the teaching app.
 *
 * The project intentionally avoids a heavyweight logging setup because the
 * most useful output for a learner is a simple, readable timeline in the
 * terminal after running ./gradlew run.
 */
public final class AppLog {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private AppLog() {
        // Utility class: all behavior is exposed through static methods.
    }

    /**
     * Print a descriptive event line to stdout.
     *
     * Format example:
     * [12:34:56.789] [JavaFX Tutor] [app] Creating primary stage
     */
    public static void info(String area, String message) {
        System.out.printf("[%s] [JavaFX Tutor] [%s] %s%n",
                LocalTime.now().format(TIME),
                area,
                message);
    }
}
