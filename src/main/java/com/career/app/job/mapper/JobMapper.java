package com.career.app.job.mapper;

import com.career.app.job.dto.JobRequest;
import com.career.app.job.dto.JobResponse;
import com.career.app.job.entity.JobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    JobEntity mapToEntity(JobRequest request);

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "id", source = "id")
    JobEntity mapToEntity(String id, JobRequest request);

    JobResponse mapToResponse(JobEntity entity);
}
