package com.rent.hata.comfort.controller;

import com.rent.hata.comfort.dto.Comfort;
import com.rent.hata.comfort.dto.Comforts;
import com.rent.hata.comfort.service.ComfortSearchFilter;
import com.rent.hata.comfort.service.ComfortService;
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
@RequestMapping("/api/comforts")
@Tag(name = "Комфорт", description = "Управление справочными данными и комфортом квартиры")
public class ComfortController {

    private final Logger logger = LoggerFactory.getLogger(ComfortController.class);
    private final ComfortService service;

    public ComfortController(ComfortService comfortService) {
        this.service = comfortService;
    }


    @Operation(summary = "Создать новое удобство")
    @PostMapping
    public ResponseEntity<Comfort> createComfort(
            @Parameter(description = "Данные об удобстве")
            @Valid @RequestBody Comfort comfort) {
        logger.info("Called createComfort");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createComfort(comfort));
    }

    @Operation(summary = "Показать удобство по идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<Comfort> getComfortById(
            @Parameter(description = "Идентификатор удобства")
            @PathVariable UUID id) {
        logger.info("Called getComfortById id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getComfortById(id));
    }

    @Operation(summary = "Показать все удобства")
    @GetMapping("/")
    public ResponseEntity<Comforts> getAllComfortsByFilter(
            @Parameter(description = "Название удобства")
            @RequestParam(name = "comfort", required = false) String tittle,

            @Parameter(description = "Количество удобств для вывода")
            @RequestParam(name = "pageSize", required = false) Integer pageSize,

            @Parameter(description = "Номер страницы поиска")
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        logger.info("Called getAllComfortsByFilter");
        var filter = new ComfortSearchFilter(tittle, pageSize, pageNumber);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllComfortsByFilter(filter));
    }

    @Operation(summary = "Обновить информацию об удобстве")
    @PutMapping("/update/{id}")
    public ResponseEntity<Comfort> updateComfort(
            @Parameter(description = "Идентификатор удобства")
            @PathVariable UUID id,
            @Parameter(description = "Информация об удобстве")
            @Valid @RequestBody Comfort comfort) {
        logger.info("Called updateComfort id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.updateComfortById(id, comfort));
    }

    @Operation(summary = "Удалить удобство")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteComfort(
            @Parameter(description = "Идентификатор удобства")
            @PathVariable UUID id) {
        logger.info("Called deleteComfort by id = {}", id);
        service.deleteComfortById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(summary = "Добавить удобство в квартиру")
    @PutMapping("/add/{idComfort}/rooms/{idRoom}/profiles/{idProfile}")
    public ResponseEntity<Void> addComfortToRoom(
            @Parameter(description = "Идентификатор удобства")
            @PathVariable UUID idComfort,
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID idRoom,
            @Parameter(description = "Идентификатор профиля")
            @PathVariable UUID idProfile){
        logger.info("Called addComfortToRoom room by id = {}, ownerId = {}", idRoom, idProfile);
        service.addComfortToRoom(idComfort, idRoom, idProfile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Удалить удобство из квартиры")
    @PutMapping("/delete/{idComfort}/rooms/{idRoom}/profiles/{idProfile}")
    public ResponseEntity<Void> deleteComfortFromRoom(
            @Parameter(description = "Идентификатор удобства")
            @PathVariable UUID idComfort,
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID idRoom,
            @Parameter(description = "Идентификатор профиля")
            @PathVariable UUID idProfile){
        logger.info("Called deleteComfortFromRoom room by id = {}, ownerId = {}", idRoom, idProfile);
        service.deleteComfortFromRoom(idComfort, idRoom, idProfile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Получить удобства квартиры")
    @GetMapping("/rooms/{idRoom}/profiles/{idProfile}")
    public ResponseEntity<Comforts> getComfortFromRoom(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID idRoom,
            @Parameter(description = "Идентификатор профиля")
            @PathVariable UUID idProfile){
        logger.info("Called getComfortToRoom room by id = {}, ownerId = {}", idRoom, idProfile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.getComfortFromRoom(idRoom, idProfile));
    }

}
