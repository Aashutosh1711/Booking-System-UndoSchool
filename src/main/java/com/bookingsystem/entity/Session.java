package com.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * A Session is a single meeting time slot within an Offering.
 *
 * IMPORTANT: startTime and endTime are always stored in UTC.
 * Timezone conversion for display happens in the service/DTO layer
 * based on the requesting user's timezone preference.
 */
@Entity
@Table(name = "sessions", indexes = {
        @Index(name = "idx_session_offering", columnList = "offering_id"),
        @Index(name = "idx_session_start_time", columnList = "start_time"),
        @Index(name = "idx_session_end_time", columnList = "end_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    /** UTC instant when this session starts */
    @Column(nullable = false)
    private Instant startTime;

    /** UTC instant when this session ends */
    @Column(nullable = false)
    private Instant endTime;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
