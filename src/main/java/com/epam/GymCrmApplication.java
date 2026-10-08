package com.epam;

import com.epam.config.AppConfig;
import com.epam.config.HibernateConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymCrmApplication {
    static void main() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class, HibernateConfig.class);
    }
}
