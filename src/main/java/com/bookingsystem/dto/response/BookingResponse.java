package com.bookingsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class BookingResponse {
    private UUID id;
    private String status;

    private UUID parentId;
    private String parentName;

    private OfferingResponse offering;

    private Instant createdAt;
}
