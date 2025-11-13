package com.project.authapi.system_log_analyzer.controller;

import com.project.authapi.system_log_analyzer.config.appConfig;
import com.project.authapi.system_log_analyzer.core.FileLoggerService;
import com.project.authapi.system_log_analyzer.core.LogEvent;
import com.project.authapi.system_log_analyzer.core.LogLevel;
import com.project.authapi.system_log_analyzer.io.WindowsEventExporter;
import com.project.authapi.system_log_analyzer.io.WindowsEventParser;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainWindowFXController {
    @FXML private Label totalLabel;
    @FXML private Label eventsLabel;
    @FXML private Label warningsLabel;
    @FXML private Label frequentLabel;

    @FXML private TableView<LogEvent> logTable;
    @FXML private TableColumn<LogEvent, String> timeColumn;
    @FXML private TableColumn<LogEvent, String> eventColumn;
    @FXML private TableColumn<LogEvent, String> descriptionColumn;
    @FXML private TableColumn<LogEvent, String> sourceColumn;

    @FXML private Button refreshButton;

    @FXML private Label loadingLabel;

    @Autowired FileLoggerService fileLoggerService;
    @Autowired private WindowsEventExporter exporter;
    @Autowired private WindowsEventParser parser;

    private List<LogEvent> logs;
    @Autowired
    private appConfig appConfig;

    @FXML
    public void initialize() {
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeColumn.setPrefWidth(150);
        eventColumn.setPrefWidth(60);
        descriptionColumn.setPrefWidth(604.0);
        sourceColumn.setPrefWidth(200);

        logTable.setFocusTraversable(false);

        logTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showDetailsPopup(newSelection);
                    }
                }
        );

        descriptionColumn.setCellFactory(tc -> { // Auto wrap in description
            TableCell<LogEvent, String> cell = new TableCell<>() {
                private final Label label = new Label();

                {
                    label.setWrapText(true);
                    label.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(label);
                    label.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                        this.setPrefHeight(newHeight.doubleValue() + 10);
                    });
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        label.setText(null);
                        setGraphic(null);
                    } else {
                        label.setText(item);
                        setGraphic(label);
                    }
                }
            };
            return cell;
        });
    }

    public void setData(List<LogEvent> events) {
        logs = events;
        fileLoggerService.saveParsedLogs(events);

        if (events == null || events.isEmpty()) {
            totalLabel.setText("Total number of log entries processed: 0");
            eventsLabel.setText("Number of error events: 0");
            warningsLabel.setText("Number of warning events: 0");
            frequentLabel.setText("Most frequent event type: N/A");
            return;
        }

        logTable.getSelectionModel().clearSelection();
        logTable.getFocusModel().focus(-1);

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

    public void showDetailsPopup(LogEvent logEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(
                "[" + logEvent.getTimestamp() + "] " +
                        "[" + logEvent.getLevel() + "] - " +
                        logEvent.getSource()
        );

        String details =
                        "-------------------------------------------\n" +
                        "Additional info:\n" +
                        logEvent.getCustomLevel() + "\n\n" +

                        "Message: \n" +
                        logEvent.getMessage() + "\n" +
                        "-------------------------------------------\n";

        TextArea textArea = new TextArea(details);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(600);
        textArea.setPrefWidth(300);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13");

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(650, 400);
        alert.setResizable(true);


        alert.showAndWait();
    }

    public void onRefreshClick() {
        refreshButton.setDisable(true);
        logTable.setDisable(true);
        loadingLabel.setText("Refreshing logs… Please wait. (Time of loading depends on number of logs)");

        Task<List<LogEvent>> task = new Task<List<LogEvent>>() {
            @Override
            protected List<LogEvent> call() throws Exception {

                List<Path> csvList = exporter.exportSelected();

                if (csvList == null) {
                    throw new IllegalStateException("Exporter returned null");
                }
                List<LogEvent> logs = new ArrayList<>();
                for (Path path : csvList) {
                    logs.addAll(parser.parseCsv(path));
                }

                return logs;
            }
        };

        task.setOnSucceeded(event -> {
            List<LogEvent> refreshedList = task.getValue();

            logs = refreshedList;
            setData(logs);

            loadingLabel.setText("");
            logTable.setDisable(false);
            refreshButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            Throwable error = task.getException();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed to refresh logs");
            alert.setContentText(error.getMessage());
            alert.showAndWait();

            loadingLabel.setText("");
            logTable.setDisable(false);
            refreshButton.setDisable(false);
        });

        new Thread(task).start();
    }
}
