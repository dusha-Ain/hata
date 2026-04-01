package com.rent.hata.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record AddressSearch(
        @Size(max = 25)
        @Schema(description = "Название страны", example = "Россия")
        String country,

        @Size(max = 25)
        @Schema(description = "Название города", example = "Воронеж")
        String city,

        @Size(max = 25)
        @Schema(description = "Название улицы", example = "ул.Ленина")
        String street,

        @Size(max = 10)
        @Schema(description = "Номер дома", example = "31А")
        String houseNumber
) {
}
