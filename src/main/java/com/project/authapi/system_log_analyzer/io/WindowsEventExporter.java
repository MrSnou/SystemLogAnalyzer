package com.project.authapi.system_log_analyzer.io;

import com.project.authapi.system_log_analyzer.config.appConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Class responsible for exporting logs from Win PowerShell
@Component
public class WindowsEventExporter {

    public enum LogType {
        APPLICATION("Application"),
        SYSTEM("System");
        //SECURITY("Security");  TODO: Handle permissions "System.UnauthorizedAccessException,Microsoft.PowerShell.Commands.GetWinEventCommand"

        private final String logName;
        LogType(String logName) {
            this.logName = logName;
        }
        public String getLogName() {
            return logName;
        }
    }

    private appConfig config;

    @Autowired
    public WindowsEventExporter(appConfig config) {
        this.config = config;
    }


    public Path exportToCsv(LogType type) {
        try {
            String baseDir = config.getLogsDir() != null && !config.getLogsDir().isEmpty()
                    ? config.getLogsDir() : "logs/exported";

            File dir = new File(baseDir, "exported");
            if (!dir.exists()) dir.mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path outputFile = Path.of(dir.getAbsolutePath(), type.getLogName() + "_" + timestamp + ".csv");

            // Powershell command
            String command = String.format(
                    "powershell.exe -Command \"Get-WinEvent -LogName %s | " +                               // Define what do we need
                            "Select-Object TimeCreated, Id, LevelDisplayName, ProviderName, Message | " +   // which tables we need from data
                            "Export-Csv -Path '%s' -NoTypeInformation -Encoding UTF8",                      // export in to csv file
                    type.getLogName(), outputFile.toAbsolutePath()
            );
                                                                            // Powershell initializer in java
            Process process = Runtime.getRuntime().exec(command);           // Create process for Powershell
            int exitCode = process.waitFor();                               // Wait for Powershell finish work.

            if (exitCode == 0) {                                            // Success otherwise write error in console
                System.out.println("Export successful: " + outputFile);
                return outputFile;
            } else {
                System.err.println("PowerShell export failed. Exit code: " + exitCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    errorReader.lines().forEach(System.err::println);
                }
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Export failed: " + e.getMessage());
            return null;
        }
    }

    public List<Path> exportSelected() {
        List<Path> paths = new ArrayList<>();

        if (config.isCsvApplication()) {
            Path p = exportToCsv(LogType.APPLICATION);
            if (p != null) paths.add(p);
        }

        if (config.isCsvSystem()) {
            Path p = exportToCsv(LogType.SYSTEM);
            if (p != null) paths.add(p);
        }

        return paths;
    }

}
