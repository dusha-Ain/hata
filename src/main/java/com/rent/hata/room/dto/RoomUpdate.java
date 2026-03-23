package com.rent.hata.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record RoomUpdate(
        @Max(value = 10)
        @Schema(description = "Количество комнат", example = "2")
        Integer capacity,

        @Min(value = 0, message = "Цена не может быть отрицательной")
        @Schema(description = "Цена аренды", example = "10 000")
        Integer price,

        @Schema(description = "Фотографии квартиры")
        String roomPictures,

        @Size(max = 500)
        @Schema(description = "Описание квартиры", example = "Уютная 2-ух комнатная квартира для студентов")
        String description
) {
}
