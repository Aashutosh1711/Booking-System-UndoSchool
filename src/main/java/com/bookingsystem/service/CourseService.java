package com.bookingsystem.service;

import com.bookingsystem.dto.request.CreateCourseRequest;
import com.bookingsystem.dto.response.CourseResponse;
import com.bookingsystem.entity.Course;
import com.bookingsystem.entity.User;
import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.repository.CourseRepository;
import com.bookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository   userRepository;

    @Transactional
    public CourseResponse createCourse(UUID teacherId, CreateCourseRequest request) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", teacherId));

        Course course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .teacher(teacher)
                .build();

        return toResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getTeacherCourses(UUID teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Course findById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
    }

    private CourseResponse toResponse(Course c) {
        return CourseResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .teacherId(c.getTeacher().getId())
                .teacherName(c.getTeacher().getName())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
