package com.project.system_log_analyzer.config;


import com.project.system_log_analyzer.SystemLogAnalyzerApp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.project.system_log_analyzer")
public class SpringConfig {

    public static boolean APP_READY = false;

}
