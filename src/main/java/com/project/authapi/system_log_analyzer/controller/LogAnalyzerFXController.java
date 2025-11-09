package com.project.authapi.system_log_analyzer.controller;

import com.project.authapi.system_log_analyzer.core.LogEvent;
import com.project.authapi.system_log_analyzer.io.WindowsEventExporter;
import com.project.authapi.system_log_analyzer.io.WindowsLogImportService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class LogAnalyzerFXController {
    @FXML
    private Stage stage;
    @FXML
    private Button scanButton;
    @FXML
    private TextField logFilesDirField;
    @FXML
    private TextField reportDirField;
    @FXML
    private Label informationLabel;


    @FXML // scan button
    private void scan(ActionEvent event) throws IOException {
        String logDir = logFilesDirField.getText();
        String reportDir = reportDirField.getText();

        if (logDir.isEmpty() || reportDir.isEmpty()) {
            informationLabel.setText("Please select both directories.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoadingScreen.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML // choose logs file direction button
    private void chooseLogDir(ActionEvent event) throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("...");
        File selectedDirectory = chooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedDirectory != null) {
            logFilesDirField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML // report file direction button
    private void chooseReportDir(ActionEvent event) throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("...");
        File selectedDirectory = chooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedDirectory != null) {
            reportDirField.setText(selectedDirectory.getAbsolutePath());
        }
    }


}
