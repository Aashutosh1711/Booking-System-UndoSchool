package com.bookingsystem.service;

import com.bookingsystem.dto.request.AddSessionRequest;
import com.bookingsystem.dto.request.CreateOfferingRequest;
import com.bookingsystem.dto.response.OfferingResponse;
import com.bookingsystem.dto.response.SessionResponse;
import com.bookingsystem.entity.*;
import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.repository.OfferingRepository;
import com.bookingsystem.repository.SessionRepository;
import com.bookingsystem.repository.UserRepository;
import com.bookingsystem.util.TimezoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final SessionRepository  sessionRepository;
    private final UserRepository     userRepository;
    private final CourseService      courseService;



    @Transactional
    public OfferingResponse createOffering(UUID teacherId, CreateOfferingRequest request) {

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", teacherId));

        Course course = courseService.findById(request.getCourseId());

        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You do not own this course");
        }

        Offering offering = Offering.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .course(course)
                .teacher(teacher)
                .maxStudents(request.getMaxStudents())
                .build();

        return toResponse(
                offeringRepository.save(offering),
                teacher.getTimezone()
        );
    }

    @Transactional
    public SessionResponse addSession(
            UUID teacherId,
            UUID offeringId,
            AddSessionRequest request) {

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering", offeringId));

        if (!offering.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You do not own this offering");
        }

        Instant startUtc = request.getStartTime().toInstant();
        Instant endUtc = request.getEndTime().toInstant();

        if (!endUtc.isAfter(startUtc)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        User teacher = offering.getTeacher();

        Session session = Session.builder()
                .offering(offering)
                .teacher(teacher)
                .startTime(startUtc)
                .endTime(endUtc)
                .build();

        Session saved = sessionRepository.save(session);

        return toSessionResponse(saved, teacher.getTimezone());
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getTeacherOfferings(UUID teacherId) {
        String tz = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", teacherId))
                .getTimezone();

        return offeringRepository.findActiveByTeacherId(teacherId).stream()
                .map(o -> toResponse(o, tz))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getTeacherUpcomingSessions(UUID teacherId) {
        String tz = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", teacherId))
                .getTimezone();

        return sessionRepository.findUpcomingByTeacherId(teacherId, Instant.now()).stream()
                .map(s -> toSessionResponse(s, tz))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<OfferingResponse> getAllActiveOfferings(String viewerTimezone) {
        return offeringRepository.findAllActive().stream()
                .map(o -> toResponse(o, viewerTimezone))
                .toList();
    }

    @Transactional(readOnly = true)
    public OfferingResponse getOfferingById(UUID offeringId, String viewerTimezone) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering", offeringId));
        return toResponse(offering, viewerTimezone);
    }



    public OfferingResponse toResponse(Offering o, String viewerTimezone) {
        List<Session> sessions = sessionRepository.findByOfferingIdOrderByStartTimeAsc(o.getId());
        List<SessionResponse> sessionResponses = sessions.stream()
                .map(s -> toSessionResponse(s, viewerTimezone))
                .toList();

        return OfferingResponse.builder()
                .id(o.getId())
                .title(o.getTitle())
                .description(o.getDescription())
                .status(o.getStatus().name())
                .maxStudents(o.getMaxStudents())
                .courseId(o.getCourse().getId())
                .courseName(o.getCourse().getName())
                .teacherId(o.getTeacher().getId())
                .teacherName(o.getTeacher().getName())
                .sessions(sessionResponses)
                .createdAt(o.getCreatedAt())
                .build();
    }

    public SessionResponse toSessionResponse(Session s, String viewerTimezone) {
        return SessionResponse.builder()
                .id(s.getId())
                .offeringId(s.getOffering().getId())
                .teacherId(s.getTeacher().getId())
                .startTimeUtc(s.getStartTime())
                .endTimeUtc(s.getEndTime())
                .startTimeLocal(TimezoneUtil.toLocalString(s.getStartTime(), viewerTimezone))
                .endTimeLocal(TimezoneUtil.toLocalString(s.getEndTime(), viewerTimezone))
                .displayTimezone(viewerTimezone)
                .build();
    }
}
