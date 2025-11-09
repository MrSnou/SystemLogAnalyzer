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
@Controller
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
        totalLabel.setText("Total Events : " + events.size());
        eventsLabel.setText(events.stream().map(LogEvent::toString).collect(Collectors.joining(LogLevel.error.toString())));
        warningsLabel.setText(events.stream().map(LogEvent::toString).collect(Collectors.joining(LogLevel.warn.toString())));
        frequentLabel.setText(events.stream().map(LogEvent::toString).collect(Collectors.joining(LogLevel.info.toString())));
//        logTable.getItems().setAll(events);
//
//        if (logTable.getItems().size() == 0 || logTable.getItems() == null) {
//            totalLabel.setText("0");
//            warningsLabel.setText("0");
//            frequentLabel.setText("0");
//        }
//
//        totalLabel.setText("Total number of log entries processed: " + events.size());
//        long errors = events.stream().filter(e -> e.getEventType().equals("ERROR")).count();
//        long warnings = events.stream().filter(e -> e.getEventType().equals("WARN")).count();
//        String frequent = events.stream()
//                .collect(Collectors.groupingBy(LogEvent::getEventType, Collectors.counting()))
//                .entrySet().stream().max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey).orElse("N/A");
//
//        eventsLabel.setText("Number of error events: " + errors);
//        warningsLabel.setText("Number of warning events: " + warnings);
//        frequentLabel.setText("Most frequent event type: " + frequent);
    }




}
