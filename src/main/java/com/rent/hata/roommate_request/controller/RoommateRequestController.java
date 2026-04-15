package com.rent.hata.roommate_request.controller;

import com.rent.hata.roommate_request.dto.RoommateRequest;
import com.rent.hata.roommate_request.dto.RoommateRequestUpdate;
import com.rent.hata.roommate_request.dto.RoommatesRequest;
import com.rent.hata.roommate_request.dto.enums.RequestStatus;
import com.rent.hata.roommate_request.service.RoommateRequestSearchFilter;
import com.rent.hata.roommate_request.service.RoommateRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/roommates_request")
@Tag(name = "Запрос соседа", description = "Управление запросами на присоединение соседей")
public class RoommateRequestController {

    private final Logger logger = LoggerFactory.getLogger(RoommateRequestController.class);
    private final RoommateRequestService service;

    public RoommateRequestController(RoommateRequestService service) {
        this.service = service;
    }

    @Operation(summary = "Создать новый запрос")
    @PostMapping
    public ResponseEntity<RoommateRequest> createRequest(
            @Parameter(description = "Данные о запросе")
            @Valid @RequestBody RoommateRequest request) {
        logger.info("Called createRequest");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createRequest(request));
    }

    @Operation(summary = "Показать запрос по идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<RoommateRequest> getRequestById(
            @Parameter(description = "Идентификатор запроса")
            @PathVariable UUID id) {
        logger.info("Called getRequestById id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getRequestById(id));
    }

    @Operation(summary = "Показать все запросы")
    @GetMapping("/")
    public ResponseEntity<RoommatesRequest> getAllRequestsByFilter(
            @Parameter(description = "Бюджет")
            @RequestParam(name = "budget", required = false) Integer budget,

            @Parameter(description = "Количество соседей")
            @RequestParam(name = "numberParticipants", required = false) Integer numberParticipants,

            @Parameter(description = "Количество запросов для вывода")
            @RequestParam(name = "pageSize", required = false) Integer pageSize,

            @Parameter(description = "Номер страницы поиска")
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        logger.info("Called getAllRequestsByFilter");
        var filter = new RoommateRequestSearchFilter(budget, numberParticipants, pageSize, pageNumber);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllRequestsByFilter(filter));
    }

    @Operation(summary = "Обновить информацию о запросе")
    @PutMapping("/update/{id}")
    public ResponseEntity<RoommateRequest> updateRequest(
            @Parameter(description = "Идентификатор запроса")
            @PathVariable UUID id,
            @Parameter(description = "Информация о запросе")
            @Valid @RequestBody RoommateRequestUpdate request) {
        logger.info("Called updateRequest id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.updateRequestById(id, request));
    }

    @Operation(summary = "Обновить статус запроса")
    @PutMapping("/update/status/{id}")
    public ResponseEntity<Void> updateStatusRequest(
            @Parameter(description = "Идентификатор запроса")
            @PathVariable UUID id,
            @Parameter(description = "Статус запроса")
            @Valid @RequestBody RequestStatus status) {
        logger.info("Called updateStatusRequest id = {}", id);
        service.updateStatusRequestById(id, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Удалить запрос")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteRequest(
            @Parameter(description = "Идентификатор запроса")
            @PathVariable UUID id) {
        logger.info("Called deleteRequest by id = {}", id);
        service.deleteRequestById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
