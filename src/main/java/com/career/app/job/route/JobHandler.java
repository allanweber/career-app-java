package com.career.app.job.route;

import com.career.app.job.dto.JobRequest;
import com.career.app.job.entity.JobEntity;
import com.career.app.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
public class JobHandler {

    private final JobService service;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ok().body(service.findAll(), JobEntity.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON).body(service.findById(id(request)), JobEntity.class);
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        return request
                .bodyToMono(JobRequest.class)
                .flatMap(service::create)
                .flatMap(job -> ServerResponse
                        .created(URI.create("/jobs/" + job.getId()))
                        .contentType(APPLICATION_JSON)
                        .bodyValue(job));
    }

    public Mono<ServerResponse> put(ServerRequest request) {
        return request
                .bodyToMono(JobRequest.class)
                .flatMap(job -> service.update(id(request), job))
                .flatMap(job -> ok().contentType(APPLICATION_JSON).bodyValue(job));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        return noContent().build(service.delete(id(request)));
    }

    public Mono<ServerResponse> event(ServerRequest request) {
        return ok().contentType(TEXT_EVENT_STREAM).body(BodyInserters.fromServerSentEvents(service.events(id(request))));
    }

    private String id(ServerRequest request) {
        return request.pathVariable("jobId");
    }
}
