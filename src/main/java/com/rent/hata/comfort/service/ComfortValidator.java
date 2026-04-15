package com.rent.hata.comfort.service;


import com.rent.hata.comfort.dto.IdComfortRoom;
import com.rent.hata.comfort.entity.ComfortEntity;
import com.rent.hata.web.exception.EntityNotFoundException;
import com.rent.hata.web.exception.EntityNotFoundInternalException;
import com.rent.hata.web.exception.EntityToManyResultException;
import com.rent.hata.web.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ComfortValidator {

    private ComfortValidator() {}

    public static void checkNotNull(Object obj) {
        if (obj == null) {
            throw new ValidationException("Data should be not null");
        }
    }

    public static Optional<ComfortEntity> validateSearchResult(
            List<ComfortEntity> comfort, String idf){
            if (comfort.size() > 1) {
                throw new EntityToManyResultException(
                        String.format("More than one comfort found by identification = %s", idf));
            }
            if (comfort.isEmpty()) {
                return Optional.empty();
            }
            return comfort.stream().findFirst();
    }

    public static ComfortEntity validateComfortExistsAfterCreate(
            Optional<ComfortEntity> comfort, String title) {
        return comfort.orElseThrow(() -> new EntityNotFoundInternalException(
                String.format("Comfort not found after create by title = %s", title)));
    }

    public static ComfortEntity validateComfortExistsAfterUpdate(
            Optional<ComfortEntity> comfort, UUID id) {
        return comfort.orElseThrow(() -> new EntityNotFoundInternalException(
                String.format("Comfort not found after update by id = %s", id)));
    }

    public static ComfortEntity validateComfortCheckExists(
            Optional<ComfortEntity> comfort, UUID id) {
        return comfort.orElseThrow(() -> new EntityNotFoundException(
                String.format("Comfort not found by id = %s", id)));
    }

    public static void validateCreationResult(boolean create, String title) {
        if (!create) {
            throw new ValidationException(
                    String.format("Comfort creation failed by title = %s", title));
        }
    }

    public static void validateUpdateResult(boolean update, UUID id) {
        if (!update) {
            throw new ValidationException(
                    String.format("Comfort update failed by id = %s", id));
        }
    }

    public static void validateDeleteResult(boolean delete, UUID id) {
        if (!delete) {
            throw new ValidationException(
                    String.format("Comfort delete failed by id = %s", id));
        }
    }

    public static void validateComfortNotExists(
            Optional<ComfortEntity> existingComfort, String title) {
        if (existingComfort.isPresent()) {
            throw new ValidationException(
                    String.format("Comfort by title = %s already exist", title));
        }
    }

    public static void validateComfortFromRoomExists(boolean check, IdComfortRoom id){
        if (!check) {
            throw new ValidationException(
                    String.format("Comfort = %s not found from room by id = %s", id.idComfort(), id.idRoom()));
        }
    }

    public static void validateComfortFromRoomNotExists(boolean check, IdComfortRoom id){
        if (check) {
            throw new ValidationException(
                    String.format("Comfort = %s already exist from room by id = %s", id.idComfort(), id.idRoom()));
        }
    }

    public static void validateDeleteFromRoomResult(boolean delete, IdComfortRoom id) {
        if (!delete) {
            throw new ValidationException(
                    String.format("Comfort = %s delete from room failed by id = %s", id.idComfort(), id.idRoom()));
        }
    }

    public static void validateAddFromRoomResult(boolean add, IdComfortRoom id) {
        if (!add) {
            throw new ValidationException(
                    String.format("Comfort = %s add from room failed by id = %s", id.idComfort(), id.idRoom()));
        }
    }

}
