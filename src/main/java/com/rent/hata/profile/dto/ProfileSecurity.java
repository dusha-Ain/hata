package com.rent.hata.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileSecurity(

        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 25, message = "Фамилия слишком длинная")
        @Schema(description = "Фамилия профиля", example = "Белов")
        String lastName,

        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 25, message = "Фамилия слишком длинная")
        @Schema(description = "Имя профиля", example = "Андрей")
        String firstName,

        @Size(max = 25, message = "Отчество слишком длинное")
        @Schema(description = "Отчество профиля", example = "Константинович")
        String patronymic,

        @NotBlank(message = "Почта не должна быть пустой")
        @Size(max = 20, message = "Почта слишком длинная")
        @Schema(description = "Почта профиля", example = "dusha_005@list.ru")
        String email,

        @NotBlank(message = "Слишком простой пароль")
        @Schema(description = "Пароль профиля", example = "CkfleO2212self3!")
        String password,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$")
        @Schema(description = "Телефон профиля", example = "+79255272450")
        String phone,

        @Schema(description = "Фото профиля")
        String profilePicture
) {
}
