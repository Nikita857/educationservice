package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateModuleRequest(
        @NotBlank(message = "Название модуля обязательно") @Size(max = 255, message = "Название модуля не более 255 символов") String title,

        String description) {
}
