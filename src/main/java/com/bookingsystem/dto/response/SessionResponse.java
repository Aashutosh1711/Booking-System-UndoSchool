package com.bookingsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class SessionResponse {
    private UUID id;
    private UUID offeringId;
    private UUID teacherId;

    private Instant startTimeUtc;
    private Instant endTimeUtc;

    /**
     * Human-readable times in the requesting user's local timezone.
     * Format: "2025-06-07T18:00:00+05:30[Asia/Kolkata]"
     */
    private String startTimeLocal;
    private String endTimeLocal;

    /** The timezone used for the local fields above */
    private String displayTimezone;
}
