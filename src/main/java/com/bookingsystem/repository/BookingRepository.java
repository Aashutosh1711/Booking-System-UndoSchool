package com.bookingsystem.repository;

import com.bookingsystem.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /** All confirmed bookings for a parent */
    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.offering o
            JOIN FETCH o.course
            WHERE b.parent.id = :parentId
              AND b.status    = 'CONFIRMED'
            ORDER BY b.createdAt DESC
            """)
    List<Booking> findConfirmedByParentId(@Param("parentId") UUID parentId);

    /** Check if a parent has already booked this offering */
    boolean existsByParentIdAndOfferingIdAndStatus(UUID parentId,
                                                    UUID offeringId,
                                                    Booking.BookingStatus status);

    /**
     * Acquires a pessimistic read lock on all confirmed bookings for a parent.
     * This prevents a concurrent transaction from inserting a new booking for
     * the same parent until our conflict check + insert completes.
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("""
            SELECT b FROM Booking b
            WHERE b.parent.id = :parentId
              AND b.status    = 'CONFIRMED'
            """)
    List<Booking> findConfirmedByParentIdWithLock(@Param("parentId") UUID parentId);

    Optional<Booking> findByIdAndParentId(UUID id, UUID parentId);
}
