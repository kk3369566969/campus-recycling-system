package com.campus.cycle.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public final class AppLogger {
    private static final Path LOG = Path.of("data", "app.log");
    private AppLogger() {}

    public static synchronized void info(String message) {
        try {
            Files.createDirectories(LOG.getParent());
            Files.writeString(LOG, LocalDateTime.now() + " INFO " + message + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // Logging must never break the business flow.
        }
    }
}
