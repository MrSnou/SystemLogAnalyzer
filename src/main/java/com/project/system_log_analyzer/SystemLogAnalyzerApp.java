package com.project.system_log_analyzer;

import com.project.system_log_analyzer.config.SpringConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SystemLogAnalyzerApp extends Application {

    private AnnotationConfigApplicationContext springContext;

    @Override
    public void init() {
        springContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WelcomeView.fxml"));
        loader.setControllerFactory(springContext::getBean);

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

    public static void main(String[] args) {
        launch(args);
    }
}

