package com.bookingsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOfferingRequest {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotBlank(message = "Offering title is required")
    private String title;

    private String description;

    @Min(value = 1, message = "Max students must be at least 1")
    private Integer maxStudents; // null = unlimited
}
