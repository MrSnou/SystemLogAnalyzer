package com.project.authapi.system_log_analyzer.core;

import com.project.authapi.system_log_analyzer.config.appConfig;
import com.project.authapi.system_log_analyzer.io.WindowsEventExporter;
import com.project.authapi.system_log_analyzer.io.WindowsEventParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class FileReportExporter {
    private appConfig config;
    private LogAnalyzerService logAnalyzerService;
    private LogReaderService logReaderService;

    @Autowired
    public FileReportExporter(appConfig config,
                              LogAnalyzerService logAnalyzerService,
                              LogReaderService logReaderService) {
        this.config = config;
        this.logAnalyzerService = logAnalyzerService;
        this.logReaderService = logReaderService;
    }

    public void export(String reportContent) {
        String dirPath = config.getReportDir();
        if (dirPath == null || dirPath.isEmpty()) {
            throw new IllegalStateException("Report directory not set!");
        }

        File reportDir = new File(dirPath);
        if (!reportDir.exists()) reportDir.mkdirs();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        File file = new File(reportDir, "SystemReport_" + timestamp + ".txt");

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(reportContent);
            fw.flush();
            System.out.println("Report saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save report: " + e.getMessage());
        }
    }

    public void generateAndExportReport() {
        System.out.println("FileReportExporter: Reading logs for report generation...");
        List<LogEvent> events = logReaderService.readAllLogsAsEvents();

        if (events == null || events.isEmpty()) {
            System.out.println("(LogAnalyzerService.analyze()) - No logs to analyze.");
            export("No logs to analyze.");
            return;
        }

        String report = logAnalyzerService.analyze(events);
        export(report);
    }
}
