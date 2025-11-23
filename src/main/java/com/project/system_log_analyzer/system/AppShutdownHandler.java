package com.project.system_log_analyzer.system;

import com.project.system_log_analyzer.config.SpringConfig;
import com.project.system_log_analyzer.config.appConfig;
import com.project.system_log_analyzer.core.FileLoggerService;
import com.project.system_log_analyzer.core.FileReportExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Component
public class AppShutdownHandler {

    private FileLoggerService fileLoggerService;
    private FileReportExporter reportExporter;
    private appConfig appConfig;
    private Boolean elevatedFlag;

    @Autowired
    public AppShutdownHandler(FileLoggerService fileLoggerService, FileReportExporter reportExporter, appConfig config) {
        this.fileLoggerService = fileLoggerService;
        this.reportExporter = reportExporter;
        this.appConfig = config;
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        if (!SpringConfig.APP_READY) {
            System.out.println("Shutdown called before app fully loaded — skipping log flush.");
            return;
        }

        if (appConfig.isNoLogs()) {
            Path dir = Paths.get(appConfig.getLogsDir());

            try {
                if (Files.exists(dir)) {
                    deleteDirectoryRecursively(dir);
                    System.out.println("NoLogs = true → temporary directory removed.");
                }
            } catch (Exception e) {
                System.err.println("Failed to delete temp directory: " + e.getMessage());
            }
            return;
        }

        if (appConfig.isRelaunch() || appConfig.getLogsDir() == null || appConfig.getLogsDir().isBlank()) {
            System.out.println("Skipping flush — elevated admin relaunch or before Spring Injection");
            return;
        } else {
            try {
                System.out.println("AppShutdownHandler - Application is shutting down! Flushing logs and exporting report...");
                fileLoggerService.flushLogToMainFile();
                System.out.println("AppShutdownHandler - Shutdown tasks completed successfully.");
            } catch (Exception e) {
                System.err.println("AppShutdownHandler - Error during shutdown tasks: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }
    // Method for correct deletion of temp files when noLogs is selected
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {}
                });
    }

}
