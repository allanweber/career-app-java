package com.career.app.job.service;

import com.career.app.job.dto.JobRequest;
import com.career.app.job.dto.JobResponse;
import com.career.app.job.mapper.JobMapper;
import com.career.app.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.Duration;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class JobService {

    private static final String JOB_NOT_FOUND = "Job not found.";

    private final JobRepository repository;

    private final JobMapper mapper = Mappers.getMapper(JobMapper.class);

    public Flux<JobResponse> findAll() {
        return repository.findAll()
                .map(mapper::mapToResponse);
    }

    public Mono<JobResponse> findById(@NotBlank String id) {
        return repository.findById(id)
                .map(mapper::mapToResponse)
                .switchIfEmpty(Mono.error(new HttpClientErrorException(NOT_FOUND, JOB_NOT_FOUND)));
    }

    public Mono<JobResponse> create(@NotNull JobRequest request) {
        return repository.save(mapper.mapToEntity(request))
                .map(mapper::mapToResponse);
    }

    public Mono<JobResponse> update(@NotBlank String id, @NotNull JobRequest request) {
        return this.repository
                .findById(id)
                .switchIfEmpty(Mono.error(new HttpClientErrorException(NOT_FOUND, JOB_NOT_FOUND)))
                .map(entity -> mapper.mapToEntity(id, request))
                .flatMap(repository::save)
                .map(mapper::mapToResponse);
    }

    public Mono<Void> delete(@NotBlank String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new HttpClientErrorException(NOT_FOUND, JOB_NOT_FOUND)))
                .flatMap(repository::delete);
    }

    public Flux<ServerSentEvent<JobResponse>> events(@NotBlank String id) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence ->
                        ServerSentEvent.<JobResponse>builder()
                                .id(String.valueOf(sequence))
                                .event("periodic-event")
                                .data(JobResponse.builder().id(id).name(id).build())
                                .build()
                );
    }
}
