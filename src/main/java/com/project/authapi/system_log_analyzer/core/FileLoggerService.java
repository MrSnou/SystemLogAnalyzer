package com.project.authapi.system_log_analyzer.core;


import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;


@Service
public class FileLoggerService implements LoggerService {
    // Number of logs (Max 5MB per log file)
    private int nOfLogPart = 0;
    // Logs directory
    private static final File logsDir = new File("C:/BackBoard/logs");
    // Actual log file
    private File logFile = new File(logsDir, "log_" + nOfLogPart + ".log");

    public FileLoggerService() {
        try {
            if (!logsDir.exists()) logsDir.mkdirs();

            if (!logFile.exists()) {
                logFile.createNewFile();
            } else {
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public void log(String message) {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(
                    getDate() + "LOG| - " + message+ "\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void log(LogLevel level, String message) {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(
                    getDate() + "LOG " + level + "| - " + message + "\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void error(LogLevel level, Throwable t) {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(
                    getDate() + "ERROR " + level + "| - " + t.getMessage()+ "\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void severe(String customLevel, String message) {
        if (customLevel == null || customLevel.isBlank()) {
            customLevel = "UNDEFINED";
        }
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(
                    getDate() + "ERROR " + customLevel + "| - " + message+ "\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + java.time.LocalDateTime.now().format(dtf) + "] ";
    }
}
