package com.project.authapi.system_log_analyzer;

import com.project.authapi.system_log_analyzer.core.*;
import com.project.authapi.system_log_analyzer.io.WindowsEventExporter;
import com.project.authapi.system_log_analyzer.io.WindowsLogImportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SystemLogAnalyzerApplication {


    public static void main(String[] args) {

        boolean runWholeAPP = false;


        if (runWholeAPP) {              // Run whole app normally
            SpringApplication.run(SystemLogAnalyzerApplication.class, args);

        } else {                        // Run inside tests
            WindowsLogImportService importer = new WindowsLogImportService();
            LogAnalyzerService analyzer = new LogAnalyzerService(new LogParserUtil(new LogReaderService()));
            var importedLogs = importer.importFromSystem(WindowsEventExporter.LogType.APPLICATION);
            analyzer.analyzeAndReport(importedLogs);
        }
    }

}
