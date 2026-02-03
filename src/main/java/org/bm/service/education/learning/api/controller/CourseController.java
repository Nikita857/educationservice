package org.bm.service.education.learning.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.learning.api.dto.response.CourseResponse;
import org.bm.service.education.learning.api.dto.request.CreateCourseRequest;
import org.bm.service.education.learning.application.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseResponse> getPublishedCourses() {
        return courseService.getPublishedCourses();
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable UUID id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseResponse created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
