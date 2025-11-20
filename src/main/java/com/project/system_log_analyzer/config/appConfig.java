package com.project.system_log_analyzer.config;

import org.springframework.stereotype.Component;

@Component
public class appConfig {
    private String logsDir;
    private String reportDir;
    private boolean csvApplication;
    private boolean csvSystem;
    private boolean csvSecurity;

    public boolean isCsvSecurity() {
        return csvSecurity;
    }

    public void setCsvSecurity(boolean csvSecurity) {
        this.csvSecurity = csvSecurity;
    }

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
