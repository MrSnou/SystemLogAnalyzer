package com.project.system_log_analyzer;

import com.project.system_log_analyzer.config.SpringConfig;
import com.project.system_log_analyzer.system.WindowsElevationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SystemLogAnalyzerApp extends Application {

    private AnnotationConfigApplicationContext springContext;

    private static Boolean elevatedFlag = false; // Admin permissions

    @Override
    public void init() {
        springContext = new AnnotationConfigApplicationContext(SpringConfig.class);

        // debug
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("app.log", true), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.setOut(out);
        System.setErr(out);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WelcomeView.fxml"));
        loader.setControllerFactory(springContext::getBean);

        boolean elevated = getParameters().getRaw().contains("--elevated");
        elevatedFlag = elevated;          // Admin profile checker

        System.out.println("ARGS = " + getParameters().getRaw()); // Admin permission check to debug file

        com.project.system_log_analyzer.config.appConfig cfg = springContext.getBean(com.project.system_log_analyzer.config.appConfig.class);

        if (elevated) {
            cfg.setCsvSecurity(true);
        }

        try {
            cfg.setCsvSecurity(elevated);
            IO.println("Elevated mode: " + elevated);
        } catch (Exception e) {
            System.err.println("Could not set elevated flag in appConfig: " + e.getMessage());
        }

        Parent root = loader.load();
        SpringConfig.APP_READY = true;

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("System Log Analyzer");
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static boolean isElevated() {
        return Boolean.TRUE.equals(elevatedFlag);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

