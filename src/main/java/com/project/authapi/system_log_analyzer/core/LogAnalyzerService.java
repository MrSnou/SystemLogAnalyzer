package com.project.authapi.system_log_analyzer.core;

import org.springframework.stereotype.Service;

import java.io.File;
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

    public void analyzeAndReport(List<LogEvent> events) {
        //List<LogEvent> allEvents = logParserUtil.parseAllLogs();
        List<LogEvent> allEvents = events;

        if (allEvents.isEmpty()) {
            IO.println("(LogAnalyzerService.analyzeAndReport()) - There are no logs to analyze");
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

        String report = buildReport(countByLevel, countBySource, latest, allEvents);

        IO.println(report); // print on console

        FileReportExporter.export(report); // print in file
    }

    public String buildReport(Map<LogLevel, Long> byLevel, Map<String, Long> bySource,
                               List<LogEvent> latest, List<LogEvent> allEvents) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n===== 📊 SYSTEM LOG REPORT =====\n");

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

        sb.append("\n===== ⚠️ ERROR SUMMARY =====\n");
        sb.append("Total Errors: ").append(errorEvents.size()).append("\n");
        sb.append("Error Rate: ").append(String.format("%.2f%%", errorRate)).append("\n");
        sb.append("Top Error Source: ").append(topErrorSource).append("\n");
        sb.append("Most Frequent Error: ").append(topErrorMessage).append("\n");
        sb.append("===============================\n");

        return sb.toString();
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
