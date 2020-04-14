package com.career.app.job.repository;

import com.career.app.job.entity.JobEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface JobRepository extends ReactiveMongoRepository<JobEntity, String> {
}
