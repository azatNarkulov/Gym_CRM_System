package com.epam.storage;

import com.epam.object.Trainee;
import com.epam.object.Trainer;
import com.epam.object.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("com.epam")
@PropertySource("file:config/application.properties")
public class StorageConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Map<Long, Trainee> traineeMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Trainer> trainerMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Training> trainingMap() {
        return new HashMap<>();
    }
}
