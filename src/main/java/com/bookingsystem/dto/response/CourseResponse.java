package com.bookingsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CourseResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID teacherId;
    private String teacherName;
    private Instant createdAt;
}
