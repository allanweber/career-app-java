package com.career.app.job.route;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration
public class JobRoutes {

    private final JobHandler jobHandler;

    @Bean
    public RouterFunction<?> routes() {
        return route(GET("/jobs").and(accept(APPLICATION_JSON)), jobHandler::getAll)
                .andRoute(GET("/jobs/{jobId}").and(accept(APPLICATION_JSON)), jobHandler::get)
                .andRoute(POST("/jobs").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), jobHandler::post)
                .andRoute(PUT("/jobs/{jobId}").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), jobHandler::put)
                .andRoute(DELETE("/jobs/{jobId}"), jobHandler::delete)
                .andRoute(GET("/jobs/{jobId}/events").and(accept(APPLICATION_JSON)), jobHandler::event);
    }
}
