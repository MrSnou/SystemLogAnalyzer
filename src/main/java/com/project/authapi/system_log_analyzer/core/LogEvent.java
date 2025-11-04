package com.project.authapi.system_log_analyzer.core;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Log Class for outside logs
public record LogEvent(LocalDateTime timestamp,
        LogLevel level,
        String source,
        String message,
        String customLevel,
        String rawLine) {


    public LogEvent {
        if (timestamp == null)
            throw new IllegalArgumentException("Timestamp cannot be null.");

        if (level == null)
            throw new IllegalArgumentException("LogLevel cannot be null.");

        if (message == null || message.isBlank())
            throw new IllegalArgumentException("Message cannot be blank.");
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] (%s) - %s",
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                level,
                customLevel == null ? "APP" : customLevel,
                message
        );
    }

    public boolean isError() {
        return level == LogLevel.error || level == LogLevel.warn;
    }

    public boolean isSystemSource() {
        return source != null && source.toLowerCase().contains("windows");
    }

}
