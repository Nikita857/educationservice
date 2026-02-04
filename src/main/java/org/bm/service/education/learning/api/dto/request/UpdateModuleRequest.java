package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateModuleRequest(
        @Size(max = 255, message = "Название модуля не более 255 символов") String title,

        String description,

        Integer orderIndex) {
}
