package com.project.authapi.system_log_analyzer.system;

import com.project.authapi.system_log_analyzer.core.FileLoggerService;
import com.project.authapi.system_log_analyzer.core.FileReportExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppShutdownHandler {

    private FileLoggerService fileLoggerService;
    private FileReportExporter reportExporter;

    @Autowired
    public AppShutdownHandler(FileLoggerService fileLoggerService, FileReportExporter reportExporter) {
        this.fileLoggerService = fileLoggerService;
        this.reportExporter = reportExporter;
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        try {
            System.out.println("AppShutdownHandler - Application is shutting down! Flushing logs and exporting report...");

            fileLoggerService.flushLogToMainFile();

            System.out.println(" AppShutdownHandler - Shutdown tasks completed successfully.");
        } catch (Exception e) {
            System.err.println("AppShutdownHandler - Error during shutdown tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
