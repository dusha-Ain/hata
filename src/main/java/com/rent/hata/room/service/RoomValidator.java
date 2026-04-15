package com.rent.hata.room.service;

import com.rent.hata.room.dto.enums.Operation;
import com.rent.hata.room.entity.AddressEntity;
import com.rent.hata.room.entity.RoomEntity;
import com.rent.hata.web.exception.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RoomValidator {

    private RoomValidator() {}

    public static void checkNotNull(Object obj) {
        if (obj == null) {
            throw new ValidationException("Data should be not null");
        }
    }

    public static RoomEntity checkRoomExists(Optional<RoomEntity> room, Object idf) {
        return room.orElseThrow(() -> new EntityNotFoundException(
                String.format("Room not found by %s = %s", getIdentifierType(idf), idf)
        ));
    }

    public static AddressEntity checkAddressExists(Optional<AddressEntity> address, Object idf) {
        return address.orElseThrow(() -> new EntityNotFoundException(
                String.format("Address not found by %s = %s", getIdentifierType(idf), idf)
        ));
    }

    public static AddressEntity checkAddressExistsInternal(Optional<AddressEntity> address,
                                                    Object idf,
                                                    Operation command) {
        return address.orElseThrow(() -> new EntityNotFoundInternalException(
                String.format("Address not found by %s = %s after %s",
                        getIdentifierType(idf),
                        idf,
                        command.toString())
        ));
    }


    public static RoomEntity checkRoomExistsInternal(Optional<RoomEntity> room,
                                              Object idf,
                                              Operation command) {
        return room.orElseThrow(() -> new EntityNotFoundInternalException(
                String.format("Room not found by %s = %s after %s",
                        getIdentifierType(idf),
                        idf,
                        command.toString())
        ));
    }

    public static void validateRoomUpdate(boolean operationResult, Object idf) {
        if (!operationResult) {
            throw new InternalUpdateException(
                    String.format("Room update by %s = %s failed", getIdentifierType(idf), idf)
            );
        }
    }

    public static void validateAddressUpdate(boolean operationResult, Object idf) {
        if (!operationResult) {
            throw new InternalUpdateException(
                    String.format("Address update by %s = %s failed", getIdentifierType(idf), idf)
            );
        }
    }

    public static void validateRoomCreate(boolean operationResult) {
        if (!operationResult) {
            throw new ValidationException("Room create failed");
        }
    }

    public static void validateAddressCreate(boolean operationResult) {
        if (!operationResult) {
            throw new ValidationException("Address create failed");
        }
    }

    public static void validateRoomDelete(boolean operationResult, Object idf) {
        if (!operationResult) {
            throw new InternalDeleteException(
                    String.format("Room delete by %s = %s failed", getIdentifierType(idf), idf)
            );
        }
    }

    public static void validateAddressDelete(boolean operationResult, Object idf) {
        if (!operationResult) {
            throw new InternalDeleteException(
                    String.format("Address delete by %s = %s failed", getIdentifierType(idf), idf)
            );
        }
    }


    private static String getIdentifierType(Object identifier) {
        return identifier instanceof UUID ? "id" : "address";
    }
}
