package com.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * A Booking represents a Parent booking an entire Offering.
 * Booking one Offering locks all its Sessions for that Parent
 * (no other Offering with overlapping sessions can be booked).
 */
@Entity
@Table(name = "bookings",
        uniqueConstraints = {
                // A parent can book the same offering only once
                @UniqueConstraint(name = "uq_booking_parent_offering",
                        columnNames = {"parent_id", "offering_id"})
        },
        indexes = {
                @Index(name = "idx_booking_parent", columnList = "parent_id"),
                @Index(name = "idx_booking_offering", columnList = "offering_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum BookingStatus {
        CONFIRMED, CANCELLED
    }
}
