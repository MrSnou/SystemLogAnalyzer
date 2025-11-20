package com.project.system_log_analyzer.core;

import com.project.system_log_analyzer.config.appConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class LogReaderService {

    private appConfig config;
    private File logsDir;

    @Autowired
    public LogReaderService(appConfig config) {
        this.config = config;
    }

    private void ensureLogsDir() {
        if (logsDir == null) {
            String path = config.getLogsDir();
            if (path == null) {
                throw new IllegalStateException("Logs directory is not set in appConfig!");
            }
            logsDir = new File(path);
            if (!logsDir.exists()) logsDir.mkdirs();
        }
    }

    public List<String> readAllLogs() {
        ensureLogsDir();
        List<String> logs = new ArrayList<>();
        for (File logFile : getLogFiles()) {
            if (!logFile.getName().startsWith("WholeLog")) continue;
            if (logFile.isFile() && logFile.length() == 0) continue;

            try {
                Files.list(Paths.get("logs/exported"))
                        .filter(path -> path.toString().endsWith(".csv"));
                logs.addAll(Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logs;
    }

    public List<String> readByLevel(LogLevel level) {
        return readLogsMatching(line -> line.contains("[" + level.toString() + "]"));
    }

    public List<String> readByKeyword(String keyword) {
        return readLogsMatching(line -> line.contains(keyword));
    }


    private List<String> readLogsMatching(Predicate<String> condition) {
        List<String> logs = new ArrayList<>();
        for (File file : getLogFiles()) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (condition.test(line)) {
                        logs.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logs;
    }

    private List<File> getLogFiles() {
        ensureLogsDir();
        File[] files = logsDir.listFiles((dir, name) -> name.endsWith(".log"));
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

    public List<LogEvent> readAllLogsAsEvents() {
        ensureLogsDir();
        File[] logFiles = logsDir.listFiles((dir, name) -> name.endsWith(".log"));

        if (logFiles == null || logFiles.length == 0) {
            System.out.println("LogReaderService: No log files found in " + logsDir.getAbsolutePath());
            return List.of();
        }

        Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified));

        List<LogEvent> events = new ArrayList<>();

        for (File file : logFiles) {
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                for (String line : lines) {
                    LogEvent e = parseLineToEvent(line);
                    if (e != null) events.add(e);
                }
            } catch (IOException e) {
                System.err.println("Error reading log file: " + file.getName() + " -> " + e.getMessage());
            }
        }

        System.out.println("LogReaderService: parsed events count = " + events.size());
        return events;
    }

    private LogEvent parseLineToEvent(String line) {
        if (line == null || line.isBlank()) return null;
        if (line.startsWith("===")) return null;

        try {
            var pattern = Pattern.compile("^\\[(.+?)\\]\\s*\\[(.+?)\\]\\s*\\((.+?)\\)\\s*-\\s*(.*)$");
            var matcher = pattern.matcher(line);

            if (matcher.find()) {
                String timestampStr = matcher.group(1).trim();
                String levelStr = matcher.group(2).trim().toLowerCase();
                String source = matcher.group(3).trim();
                String message = matcher.group(4).trim();

                LocalDateTime timestamp = LocalDateTime.parse(
                        timestampStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );

                LogLevel level;
                try {
                    level = LogLevel.valueOf(levelStr);
                } catch (IllegalArgumentException e) {
                    level = LogLevel.unknown;
                }

                return new LogEvent(timestamp, level, source, message, null, line);
            }

        } catch (Exception e) {
            System.err.println("(LogReaderService.parseLineToEvent) Failed to parse: " + line);
        }

        return null;
    }
}
