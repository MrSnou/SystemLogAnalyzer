package com.project.authapi.system_log_analyzer.io;


import com.project.authapi.system_log_analyzer.core.LogEvent;
import com.project.authapi.system_log_analyzer.core.LogLevel;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.err;

@Component
public class WindowsEventParser {

    private static final DateTimeFormatter WINDOWS_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");

    // Main method, take lines from CSV file and return list of Events
    public List<LogEvent> parseCsv(Path csvFile) {

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile.toFile()))) {
            String line;
            List<LogEvent> events = new ArrayList<>();
            boolean skipHeader = true;

            while ((line = br.readLine()) != null ) {
                if (skipHeader) {skipHeader = false; continue;} // Skip the headers line in CSV file

                String[] fields = line.split(",", 5);

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].replaceAll("\"", "").trim();
                }

                LogEvent e = parseLine(fields);
                if (e != null) events.add(e);
            }

            return events;
        } catch (IOException e) {
            err.println("Error while reading file: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    // Take line and parse it to DTO (Here LogEvent Object is created)
    private LogEvent parseLine(String[] columns) {
        if (columns.length < 5) return null;

        if (!columns[0].matches("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}(,\\d{1,3})?")) {
            System.out.println("(WindowsEventExporter.parseLine()) Skipping non-date line: " + columns[0]);
            return null;
        }

        StringBuilder rawLineSB = new StringBuilder();

        LocalDateTime timestamp;
        try {
            timestamp = parseWindowsDate(columns[0]);
        } catch (DateTimeParseException e) {
            System.err.println("(WindowsEventParser.parseLine()) Failed to parse date: " + columns[0]);
            return null;
        }

        LogLevel level = mapWindowsLevel(columns[2]);
        String provider = columns[3];
        String message = columns[4];
        if (columns[4].contains("<") && columns[4].contains(">")) {
            message = columns[4];
        } else {

        }

        String rawLine = "";

        for (int i = 0; i < columns.length; i++) {
            rawLineSB.append(columns[i] + ", ");
        }

        if (rawLineSB.length() > 2)
            rawLineSB.setLength(rawLineSB.length() - 2); // Deleting the tail ", "
        rawLine = rawLineSB.toString();

        LogEvent resultEvent = new LogEvent(timestamp, level, provider, message, null, rawLine);

        return resultEvent;


    }

    // Adapt windows name to program name
    private LogLevel mapWindowsLevel(String levelDisplayName) {
        if (levelDisplayName == null) return LogLevel.unknown;

        return switch (levelDisplayName.toLowerCase()) {
            case "error", "critical", "błąd", "krytyczny", "krytyczne" -> LogLevel.error;
            case "warning", "ostrzeżenie", "ostrzeżenia" -> LogLevel.warn;
            case "information", "informacje", "informacja", "info" -> LogLevel.info;
                    default -> LogLevel.debug;
        };
    }

    private LocalDateTime parseWindowsDate(String text) {
        String normalized = text.trim().replace(',', '.');
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss[.SSS]");
        return LocalDateTime.parse(normalized, fmt);
    }
}
