package org.bm.service.education.identity.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно") @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов") String username,

        @NotBlank(message = "Email обязателен") @Email(message = "Неверный формат email") String email,

        @NotBlank(message = "Имя обязательно") String firstName,

        @NotBlank(message = "Фамилия обязательна") String lastName,

        String middleName,

        @NotBlank(message = "Табельный номер обязателен") String personnelNumber,

        @NotBlank(message = "Пароль обязателен") @Size(min = 6, message = "Пароль должен быть не менее 6 символов") String password) {
}
