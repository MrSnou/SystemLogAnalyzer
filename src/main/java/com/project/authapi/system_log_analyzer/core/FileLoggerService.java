package com.project.authapi.system_log_analyzer.core;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/** TODO|               List
 ** TODO|   Make duplicated code method later
 ** */

@Service
public class FileLoggerService implements LoggerService {
    // Number of logs (Max 5MB per log file)
    private int nOfLogPart = 0;
    // Logs directory
    private static final File logsDir = new File("C:/BackBoard/logs");
    // Actual log file
    private File logFile = new File(logsDir, "log_" + nOfLogPart + ".log");
    // Max Log file size
    private static final long MAX_LOG_SIZE = 5 * 1024 * 1024; // 5 MB
    // Max count of files
    private static final long MAX_FILES_PER_SESSION = 20;   // 100 MB + one big at the end so (Max - 200MB)
    // Set level of save
    private LogLevel currentThreshold = LogLevel.info;

    private LogReaderService logReaderService;

    @Autowired
    public FileLoggerService(LogReaderService logReaderService) {
        try {
            this.logReaderService = logReaderService;
            createLogFileIfMissing();

        } catch (Exception ex){
            LogEntry logEntry = new LogEntry("SYSTEM", "(FileLoggerServiceNoArgsConstructor) - Exception in constructor");
            IO.println(logEntry.toString() + "\n" + ex.getMessage());
            ex.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::flushLogToMainFile)); // WholeLog.log File at System.exit(O) - No problem exit
    }

    @Override
    public void log(String message) {
        LogEntry logEntry = new LogEntry(message);
        try (FileWriter fw = new FileWriter(logFile, true)) {

            if (logEntry.getLogLevel().ordinal() < currentThreshold.ordinal()) return;      // Check if threshold is not higher

            rotateLogsIfNeeded();
            fw.write(logEntry.toString());


        } catch (IOException e) {
            LogEntry IOExLogEntry = new LogEntry("SYSTEM", "(FileLoggerService.log(String)) - Exception when writing logEntry");
            IO.println(IOExLogEntry.toString() + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void log(LogLevel level, String message) {
        LogEntry logEntry = new LogEntry(level, message);
        try (FileWriter fw = new FileWriter(logFile, true)) {

            if (level.ordinal() < currentThreshold.ordinal()) return;  // Check if threshold is not higher

            rotateLogsIfNeeded();
            fw.write(logEntry.toString());


        } catch (IOException e) {
            LogEntry IOExLogEntry = new LogEntry("SYSTEM", "(FileLoggerService.log(LogLevel, String)) - Exception when writing logEntry");
            IO.println(IOExLogEntry.toString() + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void error(LogLevel level, Throwable t) {
        LogEntry logEntry = new LogEntry(level, t.getMessage());
        try (FileWriter fw = new FileWriter(logFile, true)) {

            rotateLogsIfNeeded();
            fw.write(logEntry.toString());


        } catch (IOException e) {
            LogEntry IOExLogEntry = new LogEntry("SYSTEM", "(FileLoggerService.error(LogLevel, Throwable)) -Exception when writing logEntry");
            IO.println(IOExLogEntry.toString() + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }

    }
    @Override
    public void severe(String customLevel, String message) {
        LogEntry logEntry = new LogEntry(customLevel, message);
        try (FileWriter fw = new FileWriter(logFile, true)) {

            rotateLogsIfNeeded();
            fw.write(logEntry.toString());


        } catch (IOException e) {
            LogEntry IOExLogEntry = new LogEntry("SYSTEM", "(FileLoggerService.severe(String, String)) - Exception when writing logEntry");
            IO.println(IOExLogEntry.toString() + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void rotateLogsIfNeeded() { // Checking if log is not too big if it is, make new file
        if (logFile.length() > MAX_LOG_SIZE) {                              // Check size of logFile
            nOfLogPart++;
            logFile = new File(logsDir, "log_" + nOfLogPart + ".log");// Make new logFile

            try {
                if (nOfLogPart >= MAX_FILES_PER_SESSION) {                  // if part higher than 20, delete old one
                    deleteOldLogFiles();
                    nOfLogPart = 0;
                }

                logFile.createNewFile();
                FileWriter fw = new FileWriter(logFile, true);
                fw.write("=== New Log File Started " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + " ===\n");

            } catch (IOException e) {
                LogEntry logEntry = new LogEntry("SYSTEM", "(FileLoggerService.rotateLogsIfNeeded())Error creating log new file");
                IO.println(logEntry.toString() + "\n" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void createLogFileIfMissing() {
        if (!logsDir.exists()) logsDir.mkdirs();    // Create directory
        if (!logFile.exists()) {                    // Create file
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                LogEntry logEntry = new LogEntry("SYSTEM", "(FileLoggerService.createLogFileIfMissing()) - Exception in method createLogFileIfMissing");
                IO.println(logEntry.toString() + "\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    // Save all logs to one big file at System.exit(0)
    private void flushLogToMainFile() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        File wholeFile = new File(logsDir, "WholeLog_" + date +".log");   // Create whole log file with unique name
        try (FileWriter fw = new FileWriter(wholeFile, true)){          // Create file

            for (String log : logReaderService.readAllLogs()) {          // Fetch for all logs in pure String format
                fw.write(log);                                           // Save them in file
                fw.write(System.lineSeparator());                        // Make sure to not merge lines
                fw.write("=== SESSION ENDED AT "                     // Leave date at exit
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + " ===\n");
            }
            fw.flush();                                                  // Self-explanatory
        } catch (IOException e) {
            LogEntry logEntry = new LogEntry("SYSTEM", "(FileLoggerService.flushLogToMainFile()) - Exception at finishing WholeLog File");
            IO.println(logEntry.toString() + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteOldLogFiles() {
        File[] allLogFiles = logsDir.listFiles();
        if (allLogFiles == null || allLogFiles.length == 0) return;

        Arrays.sort(allLogFiles, Comparator.comparingLong(File::lastModified)); // Sort files from oldest to youngest
        for (int i = 0; i < allLogFiles.length - MAX_FILES_PER_SESSION; i++) {  // Check is there 20 or less files, if not delete oldest one.
            if (!allLogFiles[i].delete()) {                                     // Make sure that file is deleted
                LogEntry logEntry = new LogEntry("SYSTEM", "(deleteOldLogFiles) - Failed to delete: " + allLogFiles[i].getName());
                IO.println(logEntry.toString());
            }
        }

    }

    public void setLogLevelThreshold(LogLevel level) {
        this.currentThreshold = level;
    }





}
