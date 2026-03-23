package com.rent.hata.room.controller;

import com.rent.hata.room.dto.*;
import com.rent.hata.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Квартиры", description = "Управление квартирами профиля")
public class RoomController {

    private final Logger logger = LoggerFactory.getLogger(RoomController.class);
    private final RoomService service;

    public RoomController(RoomService roomService) {
        this.service = roomService;
    }


    @Operation(summary = "Создать новую квартиру")
    @PostMapping
    public ResponseEntity<RoomDetails> addRoom(
            @Parameter(description = "Данные о квартире")
            @Valid @RequestBody RoomDetails room, ServletRequest servletRequest) {
        logger.info("Called addRoom");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addRoom(room));
    }


    @Operation(summary = "Показать квартиру по идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<RoomDetails> getRoomById(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id) {
        logger.info("Called getRoomById id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getRoomById(id));
    }


    @Operation(summary = "Показать все квартиры + потом добавить фильтр")
    @GetMapping("/")
    public ResponseEntity<Rooms> getAllRooms() {
        logger.info("Called getAllRooms");
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllRooms());
    }


    @Operation(summary = "Обновить информацию о квартире")
    @PutMapping("/update/{id}")
    public ResponseEntity<RoomUpdate> updateRoom(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id,
            @Parameter(description = "Информация о квартире")
            @Valid @RequestBody RoomUpdate room) {
        logger.info("Called updateRoom id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.updateRoomById(id, room));
    }


    @Operation(summary = "Обновить адресс квартиры")
    @PutMapping("/update/address/{id}")
    public ResponseEntity<Address> updateRoomAddress(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id,
            @Parameter(description = "Информация об адресе")
            @Valid @RequestBody Address address) {
        logger.info("Called updateRoomAddressById id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.updateRoomAddressById(id, address));
    }


    @Operation(summary = "Удалить квартиру")
    @DeleteMapping("delete/{id}")
    public  ResponseEntity<Void> deleteRoom(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id){
        logger.info("Called deleteRoom by id = {}", id);
        service.deleteRoomById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
