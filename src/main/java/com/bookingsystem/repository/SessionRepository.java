package com.bookingsystem.repository;

import com.bookingsystem.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    /** All sessions for a given offering, ordered chronologically */
    List<Session> findByOfferingIdOrderByStartTimeAsc(UUID offeringId);

    /** Upcoming sessions for a teacher (start_time in the future) */
    @Query("""
            SELECT s FROM Session s
            WHERE s.teacher.id = :teacherId
              AND s.startTime > :now
            ORDER BY s.startTime ASC
            """)
    List<Session> findUpcomingByTeacherId(@Param("teacherId") UUID teacherId,
                                          @Param("now") Instant now);

    /**
     * Conflict-detection query.
     *
     * Returns the count of sessions that belong to offerings already booked
     * by the given parent AND overlap with any session of the new offering
     * being booked.
     *
     * Two intervals [a, b) and [c, d) overlap when: a < d AND b > c
     */
    @Query("""
            SELECT COUNT(existing) FROM Session existing
            JOIN Booking b ON b.offering.id = existing.offering.id
            WHERE b.parent.id    = :parentId
              AND b.status       = 'CONFIRMED'
              AND EXISTS (
                  SELECT 1 FROM Session newS
                  WHERE newS.offering.id = :newOfferingId
                    AND existing.startTime < newS.endTime
                    AND existing.endTime   > newS.startTime
              )
            """)
    long countConflictingSessions(@Param("parentId") UUID parentId,
                                  @Param("newOfferingId") UUID newOfferingId);
}
