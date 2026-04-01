package com.rent.hata.room.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rent.hata.room.dto.enums.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.sql.Date;
import java.util.UUID;

public record Room(
        @Null
        UUID id,

        UUID idOwner,

        UUID idAddress,

        @Max(value = 10)
        @Min(value = 1)
        @Schema(description = "Количество комнат", example = "2")
        Integer capacity,

        @Min(value = 0, message = "Цена не может быть отрицательной")
        @Schema(description = "Цена аренды", example = "10 000")
        Integer price,

        @Schema(description = "Фотографии квартиры")
        String roomPictures,

        @Null
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(example = "2026-03-20")
        Date added,

        @Null
        @Schema(description = "Количество просмотров", example = "10 000")
        Integer views,

        @Size(max = 500)
        @Schema(description = "Описание квартиры", example = "Уютная 2-ух комнатная квартира для студентов")
        String description,

        @Null
        @Schema(description = "Статус", example = "FREE")
        RoomStatus roomStatus
) {
}
