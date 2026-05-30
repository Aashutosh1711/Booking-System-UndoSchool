package com.bookingsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OfferingResponse {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private Integer maxStudents;

    private UUID courseId;
    private String courseName;

    private UUID teacherId;
    private String teacherName;

    private List<SessionResponse> sessions;

    private Instant createdAt;
}
