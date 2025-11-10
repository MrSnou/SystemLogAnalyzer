package com.project.authapi.system_log_analyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static javafx.application.Application.launch;

@SpringBootApplication
public class SystemLogAnalyzerApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        //springContext = new SpringApplicationBuilder(SystemLogAnalyzerSpringBoot.class).run();  TODO - <<[BUG] Consider defining a bean of type 'java.time.LocalDateTime' in your configuration.
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/WelcomeView.fxml"));
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
