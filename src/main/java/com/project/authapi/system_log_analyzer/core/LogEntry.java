package com.project.authapi.system_log_analyzer.core;


import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class LogEntry {
    private LogLevel logLevel;
    private String logMessage;
    private String logTime;
    private String customLevel;

    public LogEntry() {
        this.logLevel = LogLevel.info;
        this.logMessage = "Empty log entry";
        this.logTime = getDate();
        this.customLevel = "Should not be invoked. Check you code.";
    }

    public LogEntry(String message) {
        this.logLevel = LogLevel.info;
        this.logMessage = message;
        this.logTime = getDate();
        this.customLevel = null;
    }

    public LogEntry(LogLevel level, String message) {
        this.logLevel = level;
        this.logMessage = message;
        this.logTime = getDate();
        this.customLevel = null;
    }

    public LogEntry(String customLevel, String message) {
        this.logLevel = LogLevel.debug;
        this.logMessage = message;
        this.logTime = getDate();
        this.customLevel = customLevel;
    }

    public LogEntry(LogLevel level, Throwable t, String message) {
        this.logLevel = level;
        this.logMessage = message;
        this.logTime = getDate();
        this.customLevel = "Exception occured - " + t.getMessage();
    }

    @Override
    public String toString() {    // |29/10/2025 20:06:20| LOG LogLevel (CustomLevel) - Some message  |
        return "|"+ logTime + "| LOG " +
                logLevel.toString() +
                " (" + (customLevel != null ? customLevel : "null") + ")" +
                " - " + logMessage + " |" + System.lineSeparator();
    }

    private String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + java.time.LocalDateTime.now().format(dtf) + "] ";
    }


    public LogLevel getLogLevel() {
        return logLevel;
    }
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
    public String getLogMessage() {
        return logMessage;
    }
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
    public String getLogTime() {
        return logTime;
    }
    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }
    public String getCustomLevel() {
        return customLevel;
    }
    public void setCustomLevel(String customLevel) {
        this.customLevel = customLevel;
    }


}
