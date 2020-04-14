package com.career.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SuppressWarnings("PMD")
@SpringBootApplication(exclude = ErrorWebFluxAutoConfiguration.class)
@EnableWebFlux
@EnableReactiveMongoRepositories
public class AppApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
