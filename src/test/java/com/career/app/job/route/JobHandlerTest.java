package com.career.app.job.route;

import com.career.app.job.dto.JobRequest;
import com.career.app.job.dto.JobResponse;
import com.career.app.job.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
class JobHandlerTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private JobService jopService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void when_GetAll_Then_ReturnJobs() {
        JobResponse[] response = new JobResponse[]{
                new JobResponse("1", "Java"),
                new JobResponse("2", "Go")
        };

        when(jopService.findAll()).thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/jobs")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<JobResponse>>() {
                })
                .value(jobs -> assertArrayEquals(response, jobs.toArray()));
    }

    @Test
    public void given_JobId_When_GetById_Then_ReturnJob() {
        JobResponse response = new JobResponse("1", "Java");

        when(jopService.findById("1")).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/jobs/1")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(JobResponse.class)
                .value(job -> assertEquals(response, job));
    }

    @Test
    public void given_NonexistentJobId_When_GetById_Then_ReturnException() {
        when(jopService.findById("1")).thenReturn(Mono.error(new HttpClientErrorException(HttpStatus.NOT_FOUND, "job not found")));

        webTestClient.get()
                .uri("/jobs/1")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .value(error -> {
                    assertEquals(404, error.get("status"));
                    assertEquals("404 job not found", error.get("message"));
                });
    }

    @Test
    public void given_Job_When_Post_Then_ReturnJob() {
        JobRequest request = new JobRequest("Java");
        JobResponse response = new JobResponse("1", "Java");

        when(jopService.create(request)).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/jobs")
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader().valueEquals("Location", "/jobs/1")
                .expectBody(JobResponse.class)
                .value(job -> assertEquals(response, job));
    }

    @Test
    public void given_Job_When_Put_Then_ReturnUpdatedJob() {
        JobRequest outdated = new JobRequest( "Java");
        JobResponse updated = new JobResponse("1", "Java Senior");

        when(jopService.update("1", outdated)).thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/jobs/1")
                .accept(APPLICATION_JSON)
                .bodyValue(outdated)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(JobResponse.class)
                .value(job -> assertEquals(updated, job));
    }

    @Test
    public void given_Job_When_Delete_Then_ReturnNoContent() {
        when(jopService.delete("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/jobs/1")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }
}