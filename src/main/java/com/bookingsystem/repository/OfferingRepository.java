package com.bookingsystem.repository;

import com.bookingsystem.entity.Offering;
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
public interface OfferingRepository extends JpaRepository<Offering, UUID> {

    /** All active offerings for a teacher */
    @Query("""
            SELECT o FROM Offering o
            JOIN FETCH o.course
            WHERE o.teacher.id = :teacherId
              AND o.status = 'ACTIVE'
            ORDER BY o.createdAt DESC
            """)
    List<Offering> findActiveByTeacherId(@Param("teacherId") UUID teacherId);

    /** All active offerings visible to parents */
    @Query("""
            SELECT o FROM Offering o
            JOIN FETCH o.course
            WHERE o.status = 'ACTIVE'
            ORDER BY o.createdAt DESC
            """)
    List<Offering> findAllActive();

    /**
     * Acquires a pessimistic write lock on the offering row.
     * Used inside the booking transaction to serialize concurrent bookings
     * on the same offering.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offering o WHERE o.id = :id")
    Optional<Offering> findByIdWithLock(@Param("id") UUID id);
}
