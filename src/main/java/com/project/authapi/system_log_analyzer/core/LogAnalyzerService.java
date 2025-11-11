package com.project.authapi.system_log_analyzer.core;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private final LogParserUtil logParserUtil;

    public LogAnalyzerService(LogParserUtil logParserUtil) {
        this.logParserUtil = logParserUtil;
    }

    //Analyzing logs and returns report
    public String analyze(List<LogEvent> events) {
        if (events.isEmpty()) {
            return "(LogAnalyzerService.analyze()) - No logs to analyze.";
        }

        Map<LogLevel, Long> countByLevel = events.stream()
                .collect(Collectors.groupingBy(LogEvent::level, Collectors.counting()));

        Map<String, Long> countBySource = events.stream()
                .collect(Collectors.groupingBy(LogEvent::source, Collectors.counting()));

        List<LogEvent> latest = events.stream()
                .sorted(Comparator.comparing(LogEvent::timestamp).reversed())
                .limit(10)
                .toList();

        return buildReport(countByLevel, countBySource, latest, events);
    }

    private String buildReport(Map<LogLevel, Long> byLevel,
                               Map<String, Long> bySource,
                               List<LogEvent> latest,
                               List<LogEvent> allEvents) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n===== SYSTEM LOG REPORT =====\n");

        sb.append("By Level:\n");
        byLevel.forEach((level, count) -> sb.append("  ").append(level).append(": ").append(count).append("\n"));

        sb.append("\nBy Source:\n");
        bySource.forEach((src, count) -> sb.append("  ").append(src).append(": ").append(count).append("\n"));

        sb.append("\nLatest Events:\n");
        latest.forEach(e -> sb.append("  ").append(e).append("\n"));
        sb.append("===============================\n");

        List<LogEvent> errorEvents = allEvents.stream()
                .filter(LogEvent::isError)
                .toList();

        String topErrorSource = findMostFrequent(errorEvents, LogEvent::source);
        String topErrorMessage = findMostFrequent(errorEvents, LogEvent::message);

        double errorRate = allEvents.isEmpty()
                ? 0.0
                : (double) errorEvents.size() / allEvents.size() * 100;

        sb.append("\n=====  ERROR SUMMARY =====\n");
        sb.append("Total Errors: ").append(errorEvents.size()).append("\n");
        sb.append("Error Rate: ").append(String.format("%.2f%%", errorRate)).append("\n");
        sb.append("Top Error Source: ").append(topErrorSource).append("\n");
        sb.append("Most Frequent Error: ").append(topErrorMessage).append("\n");
        sb.append("===============================\n");

        return sb.toString();
    }

    public List<LogEvent> parseRawLogLines(List<String> lines) {
        return lines.stream()
                .map(this::parseLineToEvent)
                .filter(Objects::nonNull)
                .toList();
    }

    private LogEvent parseLineToEvent(String line) {
        // [2025-11-11 14:22:12] [INFO] (APP) - Message
        try {
            String[] parts = line.split(" ", 5);
            String timestampStr = parts[0].replace("[", "") + " " + parts[1].replace("]", "");
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            LogLevel level = LogLevel.valueOf(parts[2].replace("[", "").replace("]", ""));
            String message = parts.length > 4 ? parts[4] : "(no message)";

            return new LogEvent(timestamp, level, "APP", message, null, line);
        } catch (Exception e) {
            return null;
        }
    }



    private <T> String findMostFrequent(List<LogEvent> events, Function<LogEvent, T> classifier) {
        return events.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");
    }
}
