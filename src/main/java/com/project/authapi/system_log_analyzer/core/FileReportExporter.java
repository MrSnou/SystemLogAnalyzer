package com.project.authapi.system_log_analyzer.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileReportExporter {

    private static final File REPORT_DIR = new File("C:/BackBoard/logs/reports");

    public static void export(String reportContent) {
        if (!REPORT_DIR.exists()) REPORT_DIR.mkdirs();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        File file = new File(REPORT_DIR, "SystemReport_" + timestamp + ".txt");

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(reportContent);
            fw.flush();
            System.out.println("Report saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save report: " + e.getMessage());
        }
    }
}
