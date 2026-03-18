package com.rent.hata.profile.controller;

import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.service.ProfileService;
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
@RequestMapping("/api/profile")
@Tag(name = "профили", description = "Управление профилями приложения")
public class ProfileController {

    Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }


    @Operation(summary = "Создать новый профиль")
    @PostMapping
    public ResponseEntity<Profile> addProfile(
            @Parameter(description = "Данные профиля")
            @Valid @RequestBody
            ProfileSecurity profile) {
        logger.info("Called addProfile");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addProfile(profile));
    }


    @Operation(summary = "Найти профиль по идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(
            @Parameter(description = "Идентификатор профиля")
            @PathVariable UUID id) {
        logger.info("Called getProfileById = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getById(id));

    }


    @Operation(summary = "Вывести все профили")
    @GetMapping("/")
    public ResponseEntity<Profiles> getAllProfiles() {
        logger.info("Called getAllProfiles");
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllProfiles());
    }


    @Operation(summary = "Редактировать профиль")
    @PutMapping("/update/{id}")
    public ResponseEntity<Profile> updateProfile(
            @Parameter(description = "Идентифкатор профиля")
            @PathVariable UUID id,
            @Parameter(description = "Данные для обновления профиля")
            @Valid @RequestBody ProfileUpdate profile) {
        logger.info("Called updateProfile by id = {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.updateProfile(id, profile));
    }


    @Operation(summary = "Изменить пароль профиля")
    @PutMapping("update/{id}/password")
    public ResponseEntity<Void> updateProfilePassword(
            @Parameter(description = "Идентифкатор профиля")
            @PathVariable UUID id,
            @Parameter(description = "Данные о смене пароля")
            @Valid @RequestBody ProfilePassword password){
        logger.info("Called updateProfilePassword by id = {}", id);
        service.updateProfilePassword(id, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "Удалить профиль")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteProfile(
            @Parameter(description = "Идентифкатор профиля")
            @PathVariable UUID id) {
        logger.info("Called deleteProfile by id = {}", id);
        service.deleteProfile(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
