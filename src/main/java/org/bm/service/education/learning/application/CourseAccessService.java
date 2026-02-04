package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.PaginatedResponse;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.identity.infrastructure.UserRepository;
import org.bm.service.education.learning.api.dto.request.CreateCourseAccessRequest;
import org.bm.service.education.learning.api.dto.response.CourseAccessRequestResponse;
import org.bm.service.education.learning.api.dto.response.CourseAccessResponse;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.access.CourseAccess;
import org.bm.service.education.learning.domain.access.CourseAccessRequest;
import org.bm.service.education.learning.domain.access.CourseAccessRequestStatus;
import org.bm.service.education.learning.infrastructure.CourseAccessRepository;
import org.bm.service.education.learning.infrastructure.CourseAccessRequestRepository;
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
public class CourseAccessService {

    private final CourseAccessRepository courseAccessRepository;
    private final CourseAccessRequestRepository accessRequestRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // === Для MANAGER: создание заявки ===

    @Transactional
    public CourseAccessRequestResponse createAccessRequest(UUID courseId, CreateCourseAccessRequest request,
            User manager) {
        if (manager.getRole() != UserRole.MANAGER && manager.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Только менеджеры могут создавать заявки на доступ");
        }

        Course course = findCourseOrThrow(courseId);
        User user = findUserOrThrow(request.userId());

        // Проверяем, нет ли уже активной заявки
        if (accessRequestRepository.existsByCourseIdAndUserIdAndStatus(courseId, user.getId(),
                CourseAccessRequestStatus.PENDING)) {
            throw new IllegalStateException("Заявка на доступ к этому курсу уже существует");
        }

        // Проверяем, нет ли уже доступа
        if (hasAccess(courseId, user.getId())) {
            throw new IllegalStateException("У пользователя уже есть доступ к этому курсу");
        }

        CourseAccessRequest accessRequest = CourseAccessRequest.builder()
                .course(course)
                .user(user)
                .requestedBy(manager)
                .reason(request.reason())
                .status(CourseAccessRequestStatus.PENDING)
                .build();

        CourseAccessRequest saved = accessRequestRepository.save(accessRequest);
        return toResponse(saved);
    }

    // === Для INSTRUCTOR: управление заявками ===

    public PaginatedResponse<CourseAccessRequestResponse> getAccessRequests(UUID courseId, int page, int size,
            String path) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseAccessRequest> requests = accessRequestRepository.findByCourseId(courseId, pageable);
        return PaginatedResponse.of(requests.map(this::toResponse), path);
    }

    @Transactional
    public CourseAccessRequestResponse approveRequest(UUID requestId, User instructor) {
        checkInstructorOrAdmin(instructor);

        CourseAccessRequest request = findRequestOrThrow(requestId);
        if (request.getStatus() != CourseAccessRequestStatus.PENDING) {
            throw new IllegalStateException("Заявка уже обработана");
        }

        request.approve(instructor);
        accessRequestRepository.save(request);

        // Создаем доступ
        grantAccessInternal(request.getCourse(), request.getUser(), instructor);

        return toResponse(request);
    }

    @Transactional
    public CourseAccessRequestResponse rejectRequest(UUID requestId, User instructor) {
        checkInstructorOrAdmin(instructor);

        CourseAccessRequest request = findRequestOrThrow(requestId);
        if (request.getStatus() != CourseAccessRequestStatus.PENDING) {
            throw new IllegalStateException("Заявка уже обработана");
        }

        request.reject(instructor);
        accessRequestRepository.save(request);

        return toResponse(request);
    }

    // === Прямая выдача доступа (INSTRUCTOR) ===

    @Transactional
    public CourseAccessResponse grantDirectAccess(UUID courseId, UUID userId, User instructor) {
        checkInstructorOrAdmin(instructor);

        Course course = findCourseOrThrow(courseId);
        User user = findUserOrThrow(userId);

        if (hasAccess(courseId, userId)) {
            throw new IllegalStateException("У пользователя уже есть доступ к этому курсу");
        }

        CourseAccess access = grantAccessInternal(course, user, instructor);
        return toAccessResponse(access);
    }

    // === Проверка доступа ===

    public boolean hasAccess(UUID courseId, UUID userId) {
        return courseAccessRepository.findActiveAccess(courseId, userId).isPresent();
    }

    public PaginatedResponse<CourseAccessResponse> getCourseAccesses(UUID courseId, int page, int size, String path) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseAccess> accesses = courseAccessRepository.findByCourseId(courseId, pageable);
        return PaginatedResponse.of(accesses.map(this::toAccessResponse), path);
    }

    // === Private helpers ===

    private CourseAccess grantAccessInternal(Course course, User user, User grantedBy) {
        CourseAccess access = CourseAccess.builder()
                .course(course)
                .user(user)
                .grantedBy(grantedBy)
                .build();
        return courseAccessRepository.save(access);
    }

    private void checkInstructorOrAdmin(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR && user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Только инструкторы могут управлять доступом к курсам");
        }
    }

    private Course findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс", id));
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", id));
    }

    private CourseAccessRequest findRequestOrThrow(UUID id) {
        return accessRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка", id));
    }

    private CourseAccessRequestResponse toResponse(CourseAccessRequest r) {
        return new CourseAccessRequestResponse(
                r.getId(),
                r.getCourse().getId(),
                r.getCourse().getTitle(),
                r.getUser().getId(),
                r.getUser().getLastName() + " " + r.getUser().getFirstName(),
                r.getRequestedBy().getId(),
                r.getRequestedBy().getLastName() + " " + r.getRequestedBy().getFirstName(),
                r.getStatus().name(),
                r.getReason(),
                r.getCreatedAt(),
                r.getResolvedAt(),
                r.getResolvedBy() != null ? r.getResolvedBy().getLastName() + " " + r.getResolvedBy().getFirstName()
                        : null);
    }

    private CourseAccessResponse toAccessResponse(CourseAccess a) {
        return new CourseAccessResponse(
                a.getId(),
                a.getCourse().getId(),
                a.getCourse().getTitle(),
                a.getUser().getId(),
                a.getUser().getLastName() + " " + a.getUser().getFirstName(),
                a.getGrantedBy().getId(),
                a.getGrantedBy().getLastName() + " " + a.getGrantedBy().getFirstName(),
                a.getCreatedAt(),
                a.getExpiresAt(),
                a.isActive());
    }
}
