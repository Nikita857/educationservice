package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.PaginatedResponse;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.learning.api.dto.request.CreateCourseRequest;
import org.bm.service.education.learning.api.dto.request.UpdateCourseRequest;
import org.bm.service.education.learning.api.dto.response.CourseResponse;
import org.bm.service.education.learning.api.mapper.CourseMapper;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.infrastructure.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public PaginatedResponse<CourseResponse> getPublishedCourses(int page, int size, String path) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> coursePage = courseRepository.findByIsPublishedTrue(pageable);

        return PaginatedResponse.of(
                coursePage.map(courseMapper::toResponse),
                path);
    }

    public CourseResponse getCourseById(UUID id) {
        Course course = findCourseOrThrow(id);
        return courseMapper.toResponse(course);
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request, User author) {
        Course course = courseMapper.toEntity(request);
        course.setAuthor(author);
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse updateCourse(UUID id, UpdateCourseRequest request, User currentUser) {
        Course course = findCourseOrThrow(id);
        checkEditPermission(course, currentUser);

        courseMapper.updateEntity(course, request);
        Course updated = courseRepository.save(course);
        return courseMapper.toResponse(updated);
    }

    @Transactional
    public void deleteCourse(UUID id, User currentUser) {
        Course course = findCourseOrThrow(id);
        checkEditPermission(course, currentUser);
        courseRepository.delete(course);
    }

    @Transactional
    public CourseResponse publishCourse(UUID id, User currentUser) {
        Course course = findCourseOrThrow(id);
        checkEditPermission(course, currentUser);
        course.publish();
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse unpublishCourse(UUID id, User currentUser) {
        Course course = findCourseOrThrow(id);
        checkEditPermission(course, currentUser);
        course.unpublish();
        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    // === Private helpers ===

    private Course findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс", id));
    }

    private void checkEditPermission(Course course, User currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isAuthor = course.getAuthor() != null && course.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("У вас нет прав на редактирование этого курса");
        }
    }
}
