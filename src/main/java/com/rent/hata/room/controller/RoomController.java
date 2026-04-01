package com.rent.hata.room.controller;

import com.rent.hata.room.dto.*;
import com.rent.hata.room.dto.enums.RoomStatus;
import com.rent.hata.room.service.RoomSearchFilter;
import com.rent.hata.room.service.RoomService;
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
            @Valid @RequestBody RoomDetails room) {
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


    @Operation(summary = "Показать все квартиры")
    @GetMapping("/")
    public ResponseEntity<Rooms> getAllRooms(
            @Parameter(description = "Цена аренды")
            @RequestParam(name = "price", required = false) Integer price,

            @Parameter(description = "Количество комнат в квартире")
            @RequestParam(name = "capacity", required = false) Integer capacity,

            @Parameter(description = "Адресс")
            @RequestParam(name = "address", required = false) AddressSearch address,

            @Parameter(description = "Количество комнат для вывода")
            @RequestParam(name = "pageSize", required = false) Integer pageSize,

            @Parameter(description = "Номер страницы поиска")
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        logger.info("Called getAllRoomsByFilter");
        var filter = new RoomSearchFilter(price, capacity, address, pageSize, pageNumber);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllRoomsByFilter(filter));
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


    @Operation(summary = "Инкремент счётчика просмотров квартиры")
    @PutMapping("/views/inc/{id}")
    public ResponseEntity<Void> increaseViews(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id) {
        logger.info("Called increaseViews by id = {}", id);
        service.increaseViews(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "Обновить статус квартиры")
    @PutMapping("/status/{id}")
    public ResponseEntity<Void> changeStatusRoom(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id,
            @Parameter(description = "Статус квартиры")
            RoomStatus status) {
        logger.info("Called changeStatusRoom by id = {}, status = {}", id, status);
        service.changeStatusRoomById(id, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "Удалить квартиру")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "Идентификатор квартиры")
            @PathVariable UUID id) {
        logger.info("Called deleteRoom by id = {}", id);
        service.deleteRoomById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
