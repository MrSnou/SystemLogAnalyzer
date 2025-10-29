package com.project.authapi.system_log_analyzer.core;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;

@Service
@NoArgsConstructor
public class LogReaderService {
    private static final File logsDir = new File("C:/BackBoard/logs");
    private static final File logFile = new File(logsDir, "WholeLog.log");

    public List<String> readAllLogs() {
        List<String> logs = new ArrayList<>();
        for (File logFile : getLogFiles()) {
            try {
                logs.addAll(Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logs;
    }

    public List<String> readByLevel(LogLevel level) {
        return readLogsMatching(line -> line.contains(" " + level.toString() + " "));
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
        File[] files = logsDir.listFiles((dir, name) -> name.endsWith(".log"));
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }
}
