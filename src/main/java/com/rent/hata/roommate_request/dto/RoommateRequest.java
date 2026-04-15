package com.rent.hata.roommate_request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rent.hata.roommate_request.dto.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.sql.Date;
import java.util.UUID;

public record RoommateRequest(

        @Null
        @Schema(description = "Идентификатор запроса", example = "f28d67bd-c9df-43ea-837e-27877d301915")
        UUID id,

        @NotNull
        @Schema(description = "Идентификатор пользователя, подавашего запрос",
                example = "f28d67bd-c9df-43ea-837e-27877d301915")
        UUID idProfile,

        @Schema(description = "Идентификатор квартиры", example = "f28d67bd-c9df-43ea-837e-27877d301915")
        @NotNull
        UUID idRoom,

        @NotNull
        @Schema(description = "Идентификатор хозяина квартиры", example = "f28d67bd-c9df-43ea-837e-27877d301915")
        UUID idRoomOwner,

        @NotNull
        @Min( value = 0, message = "Бюджет не может быть отрицательным")
        @Schema(description = "Бюджет", example = "10000")
        Integer budget,

        @Min( value = 1, message = "Количество людей не может быть отрицательным")
        @Schema(description = "Количество соседей", example = "3")
        Integer numberParticipants,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Дата актаульности запроса", example = "2026-03-20")
        Date term,

        @Schema(description = "Комментарий к запросу", example = "Желательно чистоплотных")
        String description,

        @Null
        @Schema(description = "Статус запроса", example = "OPEN")
        RequestStatus status

) {
}
