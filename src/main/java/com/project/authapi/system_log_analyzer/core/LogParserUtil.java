package com.project.authapi.system_log_analyzer.core;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogParserUtil {
    private LogReaderService logReaderService;

    public LogParserUtil(LogReaderService logReaderService) {
        this.logReaderService = logReaderService;
    }

    public List<LogEntry> parseAllLogs() {
        List<String> rawLogs = logReaderService.readAllLogs();
        List<LogEntry> parsedEntries = new ArrayList<>();

        for (String line : rawLogs) {
            Optional<LogEntry> entry = parseLine(line);
            if (entry.isPresent()) {
                parsedEntries.add(entry.get());
            } else {
                IO.println("(LogParser) Failed to parse line: " + line);
            }
        }
        return parsedEntries;
    }

    private Optional<LogEntry> parseLine(String line) {
        Pattern p = Pattern.compile("^\\[(?<time>[^]]+)\\]\\s*\\[(?<level>[^]]+)\\]\\s*\\((?<custom>[^)]*)\\)\\s*-\\s*(?<msg>.*)\\|$");
        Matcher m = p.matcher(line.trim());

        if (!m.matches()) return Optional.empty();

        String time = m.group("time");
        String levelStr = m.group("level");
        String custom = m.group("custom");
        String message = m.group("msg");

        LogLevel level;
        try {
            level = LogLevel.valueOf(levelStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            level = LogLevel.info;
        }

        LogEntry entry = new LogEntry();
        entry.setLogTime("[" + time + "] ");
        entry.setLogLevel(level);
        entry.setCustomLevel(custom.equals("null") ? null : custom);
        entry.setLogMessage(message);

        return Optional.of(entry);
    }
}
