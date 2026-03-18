package com.rent.hata.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdate(
        @Size(max = 25, message = "Фамилия слишком длинная")
        @Schema(description = "Фамилия профиля", example = "Белов")
        String lastName,

        @Size(max = 25, message = "Фамилия слишком длинная")
        @Schema(description = "Имя профиля", example = "Андрей")
        String firstName,

        @Size(max = 25, message = "Отчество слишком длинное")
        @Schema(description = "Отчество профиля", example = "Константинович")
        String patronymic,

        @Size(max = 20, message = "Почта слишком длинная")
        @Schema(description = "Почта профиля", example = "dusha_005@list.ru")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$")
        @Schema(description = "Телефон профиля", example = "+79255272450")
        String phone,

        String profilePicture
){
}
