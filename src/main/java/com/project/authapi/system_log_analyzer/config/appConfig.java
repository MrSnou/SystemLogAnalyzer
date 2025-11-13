package com.project.authapi.system_log_analyzer.config;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class appConfig {
    private String logsDir;
    private String reportDir;
    private boolean csvApplication;
    private boolean csvSystem;

    public boolean isCsvApplication() {
        return csvApplication;
    }

    public void setCsvApplication(boolean csvApplication) {
        this.csvApplication = csvApplication;
    }

    public boolean isCsvSystem() {
        return csvSystem;
    }

    public void setCsvSystem(boolean csvSystem) {
        this.csvSystem = csvSystem;
    }

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
}
