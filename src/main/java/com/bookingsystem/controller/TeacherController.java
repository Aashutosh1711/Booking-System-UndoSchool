package com.bookingsystem.controller;

import com.bookingsystem.dto.request.AddSessionRequest;
import com.bookingsystem.dto.request.CreateCourseRequest;
import com.bookingsystem.dto.request.CreateOfferingRequest;
import com.bookingsystem.dto.response.CourseResponse;
import com.bookingsystem.dto.response.OfferingResponse;
import com.bookingsystem.dto.response.SessionResponse;
import com.bookingsystem.service.CourseService;
import com.bookingsystem.service.OfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final CourseService courseService;
    private final OfferingService offeringService;

    /**
     * Create Course
     */
    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(
            @RequestParam UUID teacherId,
            @Valid @RequestBody CreateCourseRequest request) {

        CourseResponse response =
                courseService.createCourse(teacherId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get Teacher Courses
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @RequestParam UUID teacherId) {

        return ResponseEntity.ok(
                courseService.getTeacherCourses(teacherId));
    }

    /**
     * Create Offering
     */
    @PostMapping("/offerings")
    public ResponseEntity<OfferingResponse> createOffering(
            @RequestParam UUID teacherId,
            @Valid @RequestBody CreateOfferingRequest request) {

        OfferingResponse response =
                offeringService.createOffering(teacherId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get Teacher Offerings
     */
    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getMyOfferings(
            @RequestParam UUID teacherId) {

        return ResponseEntity.ok(
                offeringService.getTeacherOfferings(teacherId));
    }

    /**
     * Add Session
     */
    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<SessionResponse> addSession(
            @RequestParam UUID teacherId,
            @PathVariable UUID offeringId,
            @Valid @RequestBody AddSessionRequest request) {

        SessionResponse response =
                offeringService.addSession(
                        teacherId,
                        offeringId,
                        request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Upcoming Sessions
     */
    @GetMapping("/sessions/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessions(
            @RequestParam UUID teacherId) {

        return ResponseEntity.ok(
                offeringService.getTeacherUpcomingSessions(teacherId));
    }
}