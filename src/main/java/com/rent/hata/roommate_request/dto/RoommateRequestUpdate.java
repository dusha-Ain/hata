package com.rent.hata.roommate_request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.sql.Date;

public record RoommateRequestUpdate(
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
        String description
) {
}
