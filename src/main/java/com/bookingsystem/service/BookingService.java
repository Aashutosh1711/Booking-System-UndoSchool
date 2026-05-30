package com.bookingsystem.service;

import com.bookingsystem.dto.response.BookingResponse;
import com.bookingsystem.entity.*;
import com.bookingsystem.exception.ConflictException;
import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.repository.BookingRepository;
import com.bookingsystem.repository.OfferingRepository;
import com.bookingsystem.repository.SessionRepository;
import com.bookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository  bookingRepository;
    private final OfferingRepository offeringRepository;
    private final SessionRepository  sessionRepository;
    private final UserRepository     userRepository;
    private final OfferingService    offeringService;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponse bookOffering(UUID parentId, UUID offeringId) {

        // 1. Load and lock the offering row (PESSIMISTIC_WRITE)
        Offering offering = offeringRepository.findByIdWithLock(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering", offeringId));

        if (offering.getStatus() != Offering.OfferingStatus.ACTIVE) {
            throw new ConflictException("Offering is no longer active");
        }

        // 2. Validate offering has at least one session
        List<Session> newSessions = sessionRepository
                .findByOfferingIdOrderByStartTimeAsc(offeringId);
        if (newSessions.isEmpty()) {
            throw new ConflictException("Cannot book an offering with no sessions");
        }

        // 3. Load parent
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", parentId));

        // 4. Lock the parent's existing bookings (PESSIMISTIC_READ) to prevent
        //    a concurrent request from passing the conflict check simultaneously
        bookingRepository.findConfirmedByParentIdWithLock(parentId);

        // 5. Duplicate booking check
        boolean alreadyBooked = bookingRepository.existsByParentIdAndOfferingIdAndStatus(
                parentId, offeringId, Booking.BookingStatus.CONFIRMED);
        if (alreadyBooked) {
            throw new ConflictException("You have already booked this offering");
        }

        // 6. Time-conflict check across all of the parent's confirmed bookings
        long conflictCount = sessionRepository.countConflictingSessions(parentId, offeringId);
        if (conflictCount > 0) {
            throw new ConflictException(
                    "Booking failed: " + conflictCount +
                    " session(s) overlap with your existing bookings. " +
                    "Please review your schedule.");
        }

        // 7. All checks passed — create the booking
        Booking booking = Booking.builder()
                .parent(parent)
                .offering(offering)
                .status(Booking.BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Parent [{}] successfully booked offering [{}]", parentId, offeringId);

        return toResponse(saved, parent.getTimezone());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getParentBookings(UUID parentId) {
        String tz = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", parentId))
                .getTimezone();

        return bookingRepository.findConfirmedByParentId(parentId).stream()
                .map(b -> toResponse(b, tz))
                .toList();
    }


    @Transactional
    public BookingResponse cancelBooking(UUID parentId, UUID bookingId) {
        Booking booking = bookingRepository.findByIdAndParentId(bookingId, parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new ConflictException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        String tz = booking.getParent().getTimezone();
        return toResponse(bookingRepository.save(booking), tz);
    }



    private BookingResponse toResponse(Booking b, String viewerTimezone) {
        return BookingResponse.builder()
                .id(b.getId())
                .status(b.getStatus().name())
                .parentId(b.getParent().getId())
                .parentName(b.getParent().getName())
                .offering(offeringService.toResponse(b.getOffering(), viewerTimezone))
                .createdAt(b.getCreatedAt())
                .build();
    }
}
