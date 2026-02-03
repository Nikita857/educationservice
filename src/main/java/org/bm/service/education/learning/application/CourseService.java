package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.learning.api.dto.response.CourseResponse;
import org.bm.service.education.learning.api.dto.request.CreateCourseRequest;
import org.bm.service.education.learning.api.mapper.CourseMapper;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.infrastructure.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByIsPublishedTrue()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    public CourseResponse getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
        return courseMapper.toResponse(course);
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        Course course = courseMapper.toEntity(request);
        // TODO: set author from security context
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    @Transactional
    public void publishCourse(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
        course.setPublished(true);
        courseRepository.save(course);
    }
}
