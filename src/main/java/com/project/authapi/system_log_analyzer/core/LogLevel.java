package com.project.authapi.system_log_analyzer.core;

public enum LogLevel {
    debug, // 0 - SYSTEM Custom errors (also in printed in console before death)
    fatal, // 1 - Application controlled deaths
    error,
    warn,
    info,
    trace,
    unknown
}
