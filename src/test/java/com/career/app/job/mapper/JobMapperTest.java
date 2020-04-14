package com.career.app.job.mapper;

import com.career.app.job.dto.JobRequest;
import com.career.app.job.dto.JobResponse;
import com.career.app.job.entity.JobEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
class JobMapperTest {

    private JobMapper mapper = Mappers.getMapper(JobMapper.class);

    @Test
    void mapToEntity() {
        JobRequest request = JobRequest.builder().name("Java").build();
        JobEntity entity = mapper.mapToEntity(request);
        assertEquals("Java", entity.getName());
        assertNull(entity.getId());
    }

    @Test
    void testMapToEntity() {
        JobRequest request = JobRequest.builder().name("Java").build();
        JobEntity entity = mapper.mapToEntity("123", request);
        assertEquals("123", entity.getId());
        assertEquals("Java", entity.getName());
    }

    @Test
    void mapToResponse() {
        String id = UUID.randomUUID().toString();
        JobEntity entity = new JobEntity("123", "Java");
        JobResponse response = mapper.mapToResponse(entity);
        assertEquals("123", response.getId());
        assertEquals("Java", response.getName());
    }
}