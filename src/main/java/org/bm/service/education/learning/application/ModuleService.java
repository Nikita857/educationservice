package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.learning.api.dto.request.CreateModuleRequest;
import org.bm.service.education.learning.api.dto.request.UpdateModuleRequest;
import org.bm.service.education.learning.api.dto.response.ModuleResponse;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Module;
import org.bm.service.education.learning.infrastructure.CourseRepository;
import org.bm.service.education.learning.infrastructure.ModuleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public List<ModuleResponse> getModulesByCourse(UUID courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ModuleResponse getModuleById(UUID id) {
        Module module = findModuleOrThrow(id);
        return toResponse(module);
    }

    @Transactional
    public ModuleResponse createModule(UUID courseId, CreateModuleRequest request, User currentUser) {
        Course course = findCourseOrThrow(courseId);
        checkEditPermission(course, currentUser);

        int nextOrder = moduleRepository.findMaxOrderIndexByCourseId(courseId) + 1;

        Module module = Module.builder()
                .title(request.title())
                .description(request.description())
                .orderIndex(nextOrder)
                .course(course)
                .build();

        Module saved = moduleRepository.save(module);
        return toResponse(saved);
    }

    @Transactional
    public ModuleResponse updateModule(UUID id, UpdateModuleRequest request, User currentUser) {
        Module module = findModuleOrThrow(id);
        checkEditPermission(module.getCourse(), currentUser);

        if (request.title() != null) {
            module.setTitle(request.title());
        }
        if (request.description() != null) {
            module.setDescription(request.description());
        }
        if (request.orderIndex() != null) {
            module.setOrderIndex(request.orderIndex());
        }

        Module saved = moduleRepository.save(module);
        return toResponse(saved);
    }

    @Transactional
    public void deleteModule(UUID id, User currentUser) {
        Module module = findModuleOrThrow(id);
        checkEditPermission(module.getCourse(), currentUser);
        moduleRepository.delete(module);
    }

    // === Private helpers ===

    private Module findModuleOrThrow(UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Модуль", id));
    }

    private Course findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс", id));
    }

    private void checkEditPermission(Course course, User currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isInstructor = currentUser.getRole() == UserRole.INSTRUCTOR;
        boolean isAuthor = course.getAuthor() != null && course.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isInstructor && !isAuthor) {
            throw new AccessDeniedException("У вас нет прав на редактирование этого курса");
        }
    }

    private ModuleResponse toResponse(Module module) {
        return new ModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getOrderIndex(),
                module.getLessons() != null ? module.getLessons().size() : 0);
    }
}
