package com.project.authapi.system_log_analyzer.core;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {
    private final LogParserUtil logParserUtil;

    public LogAnalyzerService(LogParserUtil logParserUtil) {
        this.logParserUtil = logParserUtil;
    }

    public void analyzeAndReport() {
        List<LogEvent> allEvents = logParserUtil.parseAllLogs();

        if (allEvents.isEmpty()) {
            IO.println("There are no logs to analyze");
            return;
        }

        Map<LogLevel, Long> countByLevel = allEvents.stream().collect(
                Collectors.groupingBy(LogEvent::level, Collectors.counting())
        );

        Map<String, Long> countBySource = allEvents.stream().collect(
                Collectors.groupingBy(LogEvent::source, Collectors.counting())
        );

        List<LogEvent> latest = allEvents.stream().sorted(Comparator.comparing(LogEvent::timestamp).reversed())
                .limit(10)
                .toList();

        printReport(countByLevel, countBySource, latest, allEvents);
    }

    private void printReport(Map<LogLevel, Long> byLevel, Map<String, Long> bySource,
                             List<LogEvent> latest, List<LogEvent> allEvents) {

        IO.println("\n===== 📊 SYSTEM LOG REPORT =====");
        IO.println("By Level:");
        byLevel.forEach((level, count) -> IO.println("  " + level + ": " + count));

        IO.println("\nBy Source:");
        bySource.forEach((src, count) -> IO.println("  " + src + ": " + count));

        IO.println("\nLatest Events:");
        latest.forEach(e -> IO.println("  " + e));

        IO.println("===============================");

        List<LogEvent> errorEvents = allEvents.stream()
                .filter(LogEvent::isError)
                .toList();

        String topErrorSource = findMostFrequent(errorEvents, LogEvent::source);
        String topErrorMessage = findMostFrequent(errorEvents, LogEvent::message);

        double errorRate = allEvents.isEmpty()
                ? 0.0
                : (double) errorEvents.size() / allEvents.size() * 100;

        IO.println("\n===== ⚠️ ERROR SUMMARY =====");
        IO.println("Total Errors: " + errorEvents.size());
        IO.println("Error Rate: " + String.format("%.2f%%", errorRate));
        IO.println("Top Error Source: " + topErrorSource);
        IO.println("Most Frequent Error: " + topErrorMessage);
        IO.println("===============================");
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
