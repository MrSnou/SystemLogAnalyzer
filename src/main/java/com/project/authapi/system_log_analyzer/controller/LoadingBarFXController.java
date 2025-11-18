package com.project.authapi.system_log_analyzer.controller;

import com.project.authapi.system_log_analyzer.config.appConfig;
import com.project.authapi.system_log_analyzer.core.LogEvent;
import com.project.authapi.system_log_analyzer.io.WindowsEventExporter;
import com.project.authapi.system_log_analyzer.io.WindowsEventParser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Controller
public class LoadingBarFXController {
    @FXML private ProgressBar loadingBar;
    @FXML private Label label;
    @Autowired private WindowsEventExporter exporter;
    @Autowired private WindowsEventParser parser;
    @Autowired private appConfig config;

    @FXML public void initialize(){
        IO.println("LoadingBarFXController::initialize");
        Task<List<LogEvent>> loadingTask = new Task<>() {
            @Override
            protected List<LogEvent> call() throws Exception {
                updateMessage("Scanning system logs...");
                updateProgress(0, 1);


                IO.println("exported injected - exporting logs from backend");
                List<Path> csvList = exporter.exportSelected();
                if (csvList == null) {
                    updateMessage("Failed to export logs!");
                    return List.of();
                }
                List<LogEvent> logs = new ArrayList<>();
                for  (Path path : csvList) {
                    logs.addAll(parser.parseCsv(path));
                }

                IO.println("Logs list exported");

                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(10L);
                    updateProgress(i,100);
                }
                updateMessage("Completed!");
                return logs;
            }
        };

        loadingBar.progressProperty().bind(loadingTask.progressProperty());
        label.textProperty().bind(loadingTask.messageProperty());

        loadingTask.setOnSucceeded(event -> {
            try {
                List<LogEvent> logs = loadingTask.getValue();

                var springContext = com.project.authapi.system_log_analyzer.config.ApplicationContextProvider.getApplicationContext();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
                loader.setControllerFactory(springContext::getBean);
                Parent root = loader.load();

                MainWindowFXController controller = loader.getController();
                controller.setData(logs);

                Platform.runLater(() -> {
                    Stage stage = (Stage) loadingBar.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                });
            } catch (IOException e ) {
                e.printStackTrace();
            }
        });

        Thread thread = new Thread(loadingTask);
        thread.setDaemon(true);
        thread.start();
    }
}
