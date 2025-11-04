package com.project.authapi.system_log_analyzer.core;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public List<LogEvent> parseAllLogs() {
        List<String> rawLogs = logReaderService.readAllLogs();
        List<LogEvent> parsedEntries = new ArrayList<>();

        for (String line : rawLogs) {
            Optional<LogEvent> entry = parseLine(line);
            if (entry.isPresent()) {
                parsedEntries.add(entry.get());
            } else {
                LogEvent log = new LogEvent(
                        LocalDateTime.now(), LogLevel.debug,
                        "APP", "LogParserUtil - Failed to parseAllLogs()",
                        null, null
                );
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

        LogLevel level;
        try {
            level = LogLevel.valueOf(levelStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            level = LogLevel.info;
        }

        LogEvent entry = new LogEvent(LocalDateTime.now(), level, "null", message, custom.equals("null") ? null : custom, line
                );

//        entry.setLogTime("[" + time + "] ");
//        entry.setLogLevel(level);
//        entry.setCustomLevel(custom.equals("null") ? null : custom);
//        entry.setLogMessage(message);

        return Optional.of(entry);
    }
}
