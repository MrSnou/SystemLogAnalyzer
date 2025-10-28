package com.project.authapi.system_log_analyzer.core;

public interface LoggerService {
    void log(String message);
    void log(LogLevel level, String message);
    void error(LogLevel level, Throwable t);
    // for unexpected errors
    void severe(String CustomLevel, String message);
}
