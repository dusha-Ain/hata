package com.rent.hata.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.sql.Date;
import java.util.UUID;

public record Profile(

        @Null
        UUID id,

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

        String profilePicture,

        @Null
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(example = "2026-03-20")
        Date registrationDate
) {
}
