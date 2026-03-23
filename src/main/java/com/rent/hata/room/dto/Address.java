package com.rent.hata.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record Address(
        @Null
        UUID id,

        @NotBlank(message = "Название страны не должно быть быть пустым")
        @Size(max = 25)
        @Schema(description = "Название страны", example = "Россия")
        String country,

        @NotBlank(message = "Название города не должно быть пустым")
        @Size(max = 25)
        @Schema(description = "Название города", example = "Воронеж")
        String city,

        @NotBlank(message = "Название улицы не должно быть пустым")
        @Size(max = 25)
        @Schema(description = "Название улицы", example = "ул.Ленина")
        String street,

        @NotBlank(message = "Номер дома не должен быть пустым")
        @Size(max = 10)
        @Schema(description = "Номер дома", example = "31А")
        String houseNumber,

        @Max(value = 10000)
        @Schema(description = "Номер квартиры", example = "25")
        Integer roomNumber,

        @Null
        @Schema(description = "Полный адресс", example = "25")
        String fullAddress
) {

    public String fullAddress() {
        return String.format("%s, %s, %s %s, кв.%d",
                country, city, street, houseNumber, roomNumber);
    }
}
