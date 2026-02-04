package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.Size;
import org.bm.service.education.learning.domain.LessonCompletionType;
import org.bm.service.education.learning.domain.LessonType;

public record UpdateLessonRequest(
        @Size(max = 255, message = "Название урока не более 255 символов") String title,

        String content,

        String videoUrl,

        String externalUrl,

        LessonType lessonType,

        LessonCompletionType completionType,

        Integer orderIndex) {
}
