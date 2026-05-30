package com.bookingsystem.controller;

import com.bookingsystem.dto.response.BookingResponse;
import com.bookingsystem.dto.response.OfferingResponse;
import com.bookingsystem.service.BookingService;
import com.bookingsystem.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final OfferingService offeringService;
    private final BookingService bookingService;


    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getAvailableOfferings(
            @RequestParam String timezone) {

        return ResponseEntity.ok(
                offeringService.getAllActiveOfferings(timezone));
    }

    @GetMapping("/offerings/{offeringId}")
    public ResponseEntity<OfferingResponse> getOffering(
            @PathVariable UUID offeringId,
            @RequestParam String timezone) {

        return ResponseEntity.ok(
                offeringService.getOfferingById(offeringId, timezone));
    }


    @PostMapping("/bookings/{offeringId}")
    public ResponseEntity<BookingResponse> bookOffering(
            @RequestParam UUID parentId,
            @PathVariable UUID offeringId) {

        BookingResponse response =
                bookingService.bookOffering(parentId, offeringId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @RequestParam UUID parentId) {

        return ResponseEntity.ok(
                bookingService.getParentBookings(parentId));
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponse> cancelBooking(
            @RequestParam UUID parentId,
            @PathVariable UUID bookingId) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(parentId, bookingId));
    }
}