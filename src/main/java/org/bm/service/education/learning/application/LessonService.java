package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.learning.api.dto.request.CreateLessonRequest;
import org.bm.service.education.learning.api.dto.request.UpdateLessonRequest;
import org.bm.service.education.learning.api.dto.response.LessonResponse;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.Module;
import org.bm.service.education.learning.infrastructure.LessonRepository;
import org.bm.service.education.learning.infrastructure.ModuleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public List<LessonResponse> getLessonsByModule(UUID moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LessonResponse getLessonById(UUID id) {
        Lesson lesson = findLessonOrThrow(id);
        return toResponse(lesson);
    }

    @Transactional
    public LessonResponse createLesson(UUID moduleId, CreateLessonRequest request, User currentUser) {
        Module module = findModuleOrThrow(moduleId);
        checkEditPermission(module.getCourse(), currentUser);

        int nextOrder = lessonRepository.findMaxOrderIndexByModuleId(moduleId) + 1;

        Lesson lesson = Lesson.builder()
                .title(request.title())
                .content(request.content())
                .videoUrl(request.videoUrl())
                .externalUrl(request.externalUrl())
                .lessonType(request.lessonType())
                .completionType(request.completionType())
                .orderIndex(nextOrder)
                .module(module)
                .build();

        Lesson saved = lessonRepository.save(lesson);
        return toResponse(saved);
    }

    @Transactional
    public LessonResponse updateLesson(UUID id, UpdateLessonRequest request, User currentUser) {
        Lesson lesson = findLessonOrThrow(id);
        checkEditPermission(lesson.getModule().getCourse(), currentUser);

        if (request.title() != null) {
            lesson.setTitle(request.title());
        }
        if (request.content() != null) {
            lesson.setContent(request.content());
        }
        if (request.videoUrl() != null) {
            lesson.setVideoUrl(request.videoUrl());
        }
        if (request.externalUrl() != null) {
            lesson.setExternalUrl(request.externalUrl());
        }
        if (request.lessonType() != null) {
            lesson.setLessonType(request.lessonType());
        }
        if (request.completionType() != null) {
            lesson.setCompletionType(request.completionType());
        }
        if (request.orderIndex() != null) {
            lesson.setOrderIndex(request.orderIndex());
        }

        Lesson saved = lessonRepository.save(lesson);
        return toResponse(saved);
    }

    @Transactional
    public void deleteLesson(UUID id, User currentUser) {
        Lesson lesson = findLessonOrThrow(id);
        checkEditPermission(lesson.getModule().getCourse(), currentUser);
        lessonRepository.delete(lesson);
    }

    // === Private helpers ===

    private Lesson findLessonOrThrow(UUID id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок", id));
    }

    private Module findModuleOrThrow(UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Модуль", id));
    }

    private void checkEditPermission(Course course, User currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isInstructor = currentUser.getRole() == UserRole.INSTRUCTOR;
        boolean isAuthor = course.getAuthor() != null && course.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isInstructor && !isAuthor) {
            throw new AccessDeniedException("У вас нет прав на редактирование этого курса");
        }
    }

    private LessonResponse toResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                lesson.getExternalUrl(),
                lesson.getLessonType().name(),
                lesson.getCompletionType().name(),
                lesson.getOrderIndex(),
                lesson.getTest() != null);
    }
}
