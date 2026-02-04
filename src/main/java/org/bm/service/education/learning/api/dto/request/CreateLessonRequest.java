package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.bm.service.education.learning.domain.LessonCompletionType;
import org.bm.service.education.learning.domain.LessonType;

public record CreateLessonRequest(
        @NotBlank(message = "Название урока обязательно") @Size(max = 255, message = "Название урока не более 255 символов") String title,

        String content,

        String videoUrl,

        String externalUrl,

        @NotNull(message = "Тип урока обязателен") LessonType lessonType,

        @NotNull(message = "Способ закрытия обязателен") LessonCompletionType completionType) {
}
