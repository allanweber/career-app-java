package com.career.app.job.service;

import com.career.app.job.dto.JobRequest;
import com.career.app.job.dto.JobResponse;
import com.career.app.job.entity.JobEntity;
import com.career.app.job.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository repository;

    @InjectMocks
    private JobService jobService;

    @Test
    void when_FindAllJobs_Then_ReturnJobs() {

        JobEntity[] jobEntities = new JobEntity[]{
                new JobEntity("1", "Java"),
                new JobEntity("2", "Go")
        };
        when(repository.findAll()).thenReturn(Flux.just(jobEntities));

        JobResponse[] responses = new JobResponse[]{
                new JobResponse("1", "Java"),
                new JobResponse("2", "Go")
        };

        Flux<JobResponse> all = jobService.findAll();

        StepVerifier.create(all)
                .expectNext(responses[0])
                .expectNext(responses[1])
                .verifyComplete();
    }

    @Test
    void given_JobId_When_FindJobById_Then_ReturnJob() {

        JobEntity entity = new JobEntity("1", "Java");
        when(repository.findById("1")).thenReturn(Mono.just(entity));

        JobResponse response = new JobResponse("1", "Java");

        Mono<JobResponse> byId = jobService.findById("1");

        StepVerifier.create(byId)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void given_NonExistsJobId_When_FindJobById_Then_ReturnNotFoundException() {

        when(repository.findById("1")).thenReturn(Mono.empty());

        Mono<JobResponse> byId = jobService.findById("1");

        StepVerifier.create(byId)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientErrorException &&
                                throwable.getMessage().contains("Job not found") &&
                                ((HttpClientErrorException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND)
                )
                .verify();
    }


    @Test
    void given_Job_When_Create_Then_ReturnCreatedJob() {
        JobEntity expectedEntity = new JobEntity("1", "Java");
        JobEntity entity = new JobEntity(null, "Java");

        when(repository.save(entity)).thenReturn(Mono.just(expectedEntity));

        JobRequest request = new JobRequest("Java");

        Mono<JobResponse> saved = jobService.create(request);

        StepVerifier.create(saved)
                .assertNext(jobResponse -> {
                    assertEquals("1", jobResponse.getId());
                    assertEquals("Java", jobResponse.getName());
                })
                .verifyComplete();
    }

    @Test
    void given_IdAndJob_When_Update_Then_Return_UpdatedJob() {

        String newName = "Java Senior";
        String jobId = "1";
        JobEntity savedEntity = new JobEntity(jobId, "Java");

        when(repository.findById("1")).thenReturn(Mono.just(savedEntity));

        when(repository.save(new JobEntity("1", newName))).thenReturn(Mono.just(new JobEntity("1", newName)));

        Mono<JobResponse> update = jobService.update(jobId, new JobRequest(newName));

        StepVerifier.create(update)
                .expectNextMatches(job -> job.getName().equals("Java Senior"))
                .verifyComplete();
    }

    @Test
    void given_NonExistsJobId_When_Update_Then_ReturnNotFoundException() {

        when(repository.findById("1")).thenReturn(Mono.empty());

        Mono<JobResponse> update = jobService.update("1", new JobRequest());

        StepVerifier.create(update)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientErrorException &&
                                throwable.getMessage().contains("Job not found") &&
                                ((HttpClientErrorException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND)
                )
                .verify();
    }

    @Test
    void given_JobId_When_Delete_Then_RemoveJob() {

        String jobId = "1";
        JobEntity savedEntity = new JobEntity(jobId, "Java");

        when(repository.findById("1")).thenReturn(Mono.just(savedEntity));
        when(repository.delete(savedEntity)).thenReturn(Mono.empty());

        Mono<Void> deleted = jobService.delete(jobId);

        StepVerifier.create(deleted)
                .verifyComplete();
    }

    @Test
    void given_NonExistsJobId_When_Delete_Then_ReturnNotFoundException() {

        when(repository.findById("1")).thenReturn(Mono.empty());

        Mono<Void> delete = jobService.delete("1");

        StepVerifier.create(delete)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientErrorException &&
                                throwable.getMessage().contains("Job not found") &&
                                ((HttpClientErrorException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND)
                )
                .verify();
    }
}