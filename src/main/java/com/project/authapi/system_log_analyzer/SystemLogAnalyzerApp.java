package com.project.authapi.system_log_analyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        springContext = new SpringApplicationBuilder(SystemLogAnalyzerSpringBoot.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("System Log Analyzer");
        primaryStage.setScene(scene);
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
