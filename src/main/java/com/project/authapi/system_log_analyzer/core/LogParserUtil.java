package com.project.authapi.system_log_analyzer.core;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogParserUtil {
    private LogReaderService logReaderService;
    private static final DateTimeFormatter LOG_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogParserUtil(LogReaderService logReaderService) {
        this.logReaderService = logReaderService;
    }

    public List<LogEvent> parseAllLogs() {
        List<String> rawLogs = logReaderService.readAllLogs();
        List<LogEvent> parsedEntries = new ArrayList<>();

        for (String line : rawLogs) {
            if (line.startsWith("===") || line.isBlank()) continue;

            Optional<LogEvent> entry = parseLine(line);
            if (entry.isPresent()) {
                parsedEntries.add(entry.get());
            } else {
                LogEvent log = new LogEvent(
                        LocalDateTime.now(), LogLevel.debug,
                        "APP", "LogParserUtil - Failed to parseAllLogs()",
                        null, null
                );
                parsedEntries.add(log);
                IO.println("(LogParser) Failed to parse line: " + line);
            }
        }
        return parsedEntries;
    }

    private Optional<LogEvent> parseLine(String line) {
        Pattern p = Pattern.compile("^\\[(?<time>[^]]+)\\]\\s*\\[(?<level>[^]]+)\\]\\s*\\((?<custom>[^)]*)\\)\\s*-\\s*(?<msg>.*)$");

        Matcher m = p.matcher(line.trim());

        if (!m.matches()) return Optional.empty();

        String time = m.group("time");
        String levelStr = m.group("level");
        String custom = m.group("custom");
        String message = m.group("msg");

        LocalDateTime timestamp;
        boolean parseError = false;
        try {
            timestamp = LocalDateTime.parse(time, LOG_TIME_FMT);
        } catch (DateTimeParseException e) {
            timestamp = LocalDateTime.now();
            parseError = true;
        }

        LogLevel level;
        try {
            level = LogLevel.valueOf(levelStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            level = LogLevel.info;
        }


        String customLevel = custom.equals("null") ? null : custom;
        if (parseError) customLevel = "PARSER_ERROR";
        LogEvent entry = new LogEvent(
                timestamp, level,
                "null", message,
                custom.equals("null") ? null : custom, line
                );

        return Optional.of(entry);
    }
}
