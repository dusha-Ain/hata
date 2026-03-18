package com.rent.hata.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfilePassword(
        @NotBlank
        @Schema(description = "Текущий пароль", example = "oldPass123")
        String currentPassword,

        @NotBlank(message = "Новый пароль обязателен")
        @Size(min = 8, message = "Пароль минимум 8 символов")
        @Schema(description = "Новый пароль", example = "F3od2v!#kf123!")
        String newPassword,

        @NotBlank(message = "Подтверждение пароля обязательно")
        @Schema(description = "Подтверждение нового пароля", example = "F3od2v!#kf123!")
        String confirmPassword
) {
}
