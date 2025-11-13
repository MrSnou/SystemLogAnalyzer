package com.project.authapi.system_log_analyzer.controller;

import com.project.authapi.system_log_analyzer.config.ApplicationContextProvider;
import com.project.authapi.system_log_analyzer.config.appConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class LogAnalyzerFXController {
    @FXML private Button scanButton;
    @FXML private TextField logFilesDirField;
    @FXML private TextField reportDirField;
    @FXML private Label informationLabel;
    @FXML private CheckBox appButton;
    @FXML private CheckBox systemButton;

    @Autowired public appConfig appConfig;

    @FXML
    public void initialize() {
        System.out.println("Controller initialized, appConfig = " + appConfig);
    }


    @FXML // scan button
    private void scan(ActionEvent event) throws IOException {
        String logDir = logFilesDirField.getText();
        String reportDir = reportDirField.getText();

        if (logDir.isEmpty() || reportDir.isEmpty()) {
            informationLabel.setText("Please select both directories.");
            return;
        }

        if (!appButton.isSelected() && !systemButton.isSelected()) {
            informationLabel.setText("Please select at least one type of logs");
            return;
        }

        ApplicationContext springContext = ApplicationContextProvider.getApplicationContext();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoadingScreen.fxml"));
        loader.setControllerFactory(springContext::getBean); // <-- magiczna linia
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
            appConfig.setLogsDir(logFilesDirField.getText());
        }
    }

    @FXML // report file direction button
    private void chooseReportDir(ActionEvent event) throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("...");
        File selectedDirectory = chooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedDirectory != null) {
            reportDirField.setText(selectedDirectory.getAbsolutePath());
            appConfig.setReportDir(reportDirField.getText());
        }
    }

    @FXML
    private void appButtonON(ActionEvent event) throws IOException {
        appConfig.setCsvApplication(appButton.isSelected());
        IO.println("Application logs export : " + appConfig.isCsvApplication());

    }

    @FXML
    private void systemButtonON(ActionEvent event) throws IOException {
        appConfig.setCsvSystem(systemButton.isSelected());
        IO.println("System logs export : " + appConfig.isCsvSystem());
    }
}
