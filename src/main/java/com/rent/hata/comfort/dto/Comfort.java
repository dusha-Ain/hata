package com.rent.hata.comfort.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record Comfort(
        @Null
        UUID id,

        @NotBlank(message = "Название не должно быть пустым")
        @Size(max = 20)
        @Schema(description = "Название удобства", example = "wi-fi")
        String title,

        @Schema(description = "ссылка на иконку")
        String icon,

        @Schema(description = "Описание комфорта", example = "Позволяет подключаться к беспроводному интернету")
        @Size(max = 250)
        String description
) {
}
