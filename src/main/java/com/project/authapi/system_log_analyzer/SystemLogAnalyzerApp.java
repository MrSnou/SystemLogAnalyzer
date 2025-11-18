package com.project.authapi.system_log_analyzer;

import com.project.authapi.system_log_analyzer.controller.WelcomeViewFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SystemLogAnalyzerApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SystemLogAnalyzerSpringBoot.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("/view/WelcomeView.fxml"));
        loader.setControllerFactory(springContext::getBean);
        loader.setController(springContext.getBean(WelcomeViewFXController.class));
        Parent root = loader.load();

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
