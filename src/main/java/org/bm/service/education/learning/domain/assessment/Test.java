package org.bm.service.education.learning.domain.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.Module;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests", indexes = {
        @Index(name = "idx_tests_lesson", columnList = "lesson_id"),
        @Index(name = "idx_tests_module", columnList = "module_id"),
        @Index(name = "idx_tests_course", columnList = "course_id")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Test extends EntityBase {

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private int passingScore = 70;

    @Setter
    private Integer timeLimitMinutes;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private int maxAttempts = 0;

    // XOR: только одна связь должна быть заполнена
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Builder.Default
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Question> questions = new ArrayList<>();

    // Business methods
    public void addQuestion(Question question) {
        questions.add(question);
        question.setTest(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTest(null);
    }

    // Helper methods
    public TestTargetType getTargetType() {
        if (lesson != null)
            return TestTargetType.LESSON;
        if (module != null)
            return TestTargetType.MODULE;
        if (course != null)
            return TestTargetType.COURSE;
        throw new IllegalStateException("Test must have a target");
    }

    public String getTargetTitle() {
        if (lesson != null)
            return lesson.getTitle();
        if (module != null)
            return module.getTitle();
        if (course != null)
            return course.getTitle();
        return null;
    }
}
