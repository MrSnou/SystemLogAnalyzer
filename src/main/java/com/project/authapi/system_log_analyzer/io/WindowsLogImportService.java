package com.project.authapi.system_log_analyzer.io;

import com.project.authapi.system_log_analyzer.core.LogEvent;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class WindowsLogImportService {
    private final WindowsEventExporter exporter = new WindowsEventExporter();
    private final WindowsEventParser parser = new WindowsEventParser();

    public List<LogEvent> importFromSystem(WindowsEventExporter.LogType type) {
        Path csv = exporter.exportToCsv(type);
        if (csv == null) return Collections.emptyList();
        return parser.parseCsv(csv);
    }
}

