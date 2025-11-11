package com.project.authapi.system_log_analyzer.config;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class appConfig {
    private String logsDir;
    private String reportDir;

    public String getLogsDir() {
        return logsDir;
    }

    public void setLogsDir(String logsDir) {
        this.logsDir = logsDir;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public boolean isReady() {
        return logsDir != null && reportDir != null;
    }
}
