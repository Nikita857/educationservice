package org.bm.service.education.learning.domain.progress;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "screenshot_submissions", indexes = {
        @Index(name = "idx_screenshot_lesson_progress", columnList = "lesson_progress_id"),
        @Index(name = "idx_screenshot_status", columnList = "status")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScreenshotSubmission extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_progress_id", nullable = false)
    private LessonProgress lessonProgress;

    @Setter
    @Column(nullable = false)
    private String filePath;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreenshotStatus status = ScreenshotStatus.PENDING;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Setter
    private LocalDateTime reviewedAt;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String reviewComment;

    // Business methods
    public void approve(User reviewer, String comment) {
        this.status = ScreenshotStatus.APPROVED;
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
        this.reviewComment = comment;
    }

    public void reject(User reviewer, String comment) {
        this.status = ScreenshotStatus.REJECTED;
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
        this.reviewComment = comment;
    }
}
