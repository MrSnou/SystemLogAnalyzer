package com.project.authapi.system_log_analyzer.controller;

import com.project.authapi.system_log_analyzer.core.LogEvent;
import com.project.authapi.system_log_analyzer.core.LogLevel;
import com.project.authapi.system_log_analyzer.io.WindowsLogImportService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO - HERE
public class MainWindowFXController {
    @FXML private Label totalLabel;
    @FXML private Label eventsLabel;
    @FXML private Label warningsLabel;
    @FXML private Label frequentLabel;

    @FXML private TableView<LogEvent> logTable;
    @FXML private TableColumn<LogEvent, String> eventColumn;
    @FXML private TableColumn<LogEvent, String> descriptionColumn;
    @FXML private TableColumn<LogEvent, String> sourceColumn;

    @FXML
    public void initialize() {
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
    }

    public void setData(List<LogEvent> events) {
        if (events == null || events.isEmpty()) {
            totalLabel.setText("Total number of log entries processed: 0");
            eventsLabel.setText("Number of error events: 0");
            warningsLabel.setText("Number of warning events: 0");
            frequentLabel.setText("Most frequent event type: N/A");
            return;
        }

        logTable.getItems().setAll(events);

        totalLabel.setText("Total number of log entries processed:" + events.size());

        long errors = events.stream()
                .filter(e -> e.getLevel() == LogLevel.error)
                .count();

        long warnings = events.stream()
                .filter(e -> e.getLevel() == LogLevel.warn).count();

        String mostFrequent = events.stream()
                .collect(Collectors.groupingBy(LogEvent::getLevel, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(Enum::name)
                .orElse("N/A");

        eventsLabel.setText("Number of error events: " + errors);
        warningsLabel.setText("Number of warning events: " + warnings);
        frequentLabel.setText("Most frequent event type: " + mostFrequent);



    }




}
