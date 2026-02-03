package org.bm.service.education.learning.api.mapper;

import org.bm.service.education.learning.api.dto.CourseResponse;
import org.bm.service.education.learning.api.dto.CreateCourseRequest;
import org.bm.service.education.learning.domain.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnailUrl(),
                course.isPublished(),
                course.getEstimatedDurationMinutes(),
                course.getAuthor() != null ? course.getAuthor().getId() : null,
                course.getAuthor() != null
                        ? course.getAuthor().getLastName() + " " + course.getAuthor().getFirstName()
                        : null,
                course.getModules() != null ? course.getModules().size() : 0);
    }

    public Course toEntity(CreateCourseRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setThumbnailUrl(request.thumbnailUrl());
        course.setEstimatedDurationMinutes(request.estimatedDurationMinutes());
        return course;
    }
}
