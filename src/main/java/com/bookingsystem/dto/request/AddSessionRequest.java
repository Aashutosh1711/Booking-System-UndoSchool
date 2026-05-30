package com.bookingsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class AddSessionRequest {


    @NotNull(message = "Start time is required (ISO-8601 with offset, e.g. 2025-06-07T18:00:00+05:30)")
    private OffsetDateTime startTime;

    @NotNull(message = "End time is required (ISO-8601 with offset, e.g. 2025-06-07T19:00:00+05:30)")
    private OffsetDateTime endTime;
}
