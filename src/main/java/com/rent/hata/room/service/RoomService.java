package com.rent.hata.room.service;

import com.rent.hata.room.dao.RoomDao;
import com.rent.hata.room.dto.Address;
import com.rent.hata.room.dto.RoomDetails;
import com.rent.hata.room.dto.RoomUpdate;
import com.rent.hata.room.dto.Rooms;
import com.rent.hata.room.entity.AddressEntity;
import com.rent.hata.room.entity.RoomEntity;
import com.rent.hata.room.entity.RoomsEntity;
import com.rent.hata.web.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomMapper mapper;
    private final RoomDao roomDao;

    public RoomService(RoomMapper mapper, RoomDao roomDao) {
        this.mapper = mapper;
        this.roomDao = roomDao;
    }


    @Transactional
    public RoomDetails addRoom(RoomDetails room) {
        AddressEntity address = mapper.toEntity(room.address());
        Optional<RoomEntity> maybeRoom = roomDao.getRoomByAddressWithLock(address);
        if (maybeRoom.isPresent()) {
            throw new ValidationException(String.format("Room with address = %s already exist", address.fullAddress()));
        }
        var roomEntity = mapper.toEntity(room);
        if (!roomDao.createAddress(address)) {
            throw new ValidationException("Address creation failed");
        }
        AddressEntity savedAddress = roomDao.getAddressByAddressWithLock(address)
                .orElseThrow(() -> new EntityNotFoundInternalException("Not found address after create"));
        if (!roomDao.createRoom(roomEntity, savedAddress.getId())) {
            throw new ValidationException("Room creation failed");
        }
        var savedRoom = roomDao.getRoomByAddress(savedAddress)
                .orElseThrow(() -> new EntityNotFoundInternalException(
                        String.format("Room not found after create by address = %s", address.fullAddress())
                ));
        return mapper.toDomainRoomDetails(savedRoom, savedAddress);
    }


    @Transactional
    public RoomDetails getRoomById(UUID id) {
        var room = roomDao.getRoomById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found room by id = %s", id)
                ));
        var address = roomDao.getAddressById(room.getIdAddress())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found address by id = %s", room.getIdAddress())
                ));
        return mapper.toDomainRoomDetails(room, address);
    }


    public Rooms getAllRooms() {
        RoomsEntity roomsEntity = roomDao.getAllRooms();
        return new Rooms(roomsEntity.list.stream()
                .map(mapper::toDomainRoom).toList());
    }


    @Transactional
    public RoomUpdate updateRoomById(UUID id, RoomUpdate room) {
        var maybeRoom = roomDao.getRoomByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found room by id = %s", id)
                ));
        var roomUpdate = mapper.toEntity(maybeRoom, room);
        if (!roomDao.updateRoom(roomUpdate)) {
            throw new InternalUpdateException(
                    String.format("Room update by id = %s failed", id)
            );
        }
        var savedRoom = roomDao.getRoomById(id)
                .orElseThrow(() -> new EntityNotFoundInternalException(
                        String.format("Room not found after create by id = %s", id)));
        return mapper.toDomainRoomUpdate(savedRoom);

    }

    @Transactional
    public Address updateRoomAddressById(UUID id, Address address) {
        var maybeRoom = roomDao.getRoomByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found room by id = %s", id)
                ));
        UUID idAddress = maybeRoom.getIdAddress();
        var maybeAddress = roomDao.getAddressByIdWithLock(idAddress)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Address not found by id = %s", idAddress)));
        var addressUpdate = mapper.toEntity(maybeAddress, address);
        if (!roomDao.updateRoomAddress(addressUpdate)) {
            throw new InternalUpdateException(
                    String.format("Address update by id = %s failed", id));
        }
        var savedAddress = roomDao.getAddressById(idAddress)
                .orElseThrow(() -> new EntityNotFoundInternalException(
                        String.format("Room not found after create by id = %s", idAddress)));
        return mapper.toDomainAddress(savedAddress);
    }


    @Transactional
    public void deleteRoomById(UUID id) {
        var maybeRoom = roomDao.getRoomByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found room by id = %s", id)
                ));
        var maybeAddress = roomDao.getAddressByIdWithLock(maybeRoom.getIdAddress())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found address by id = %s", id)
                ));
        if (!roomDao.deleteRoom(id)) {
            throw new InternalDeleteException(
                    String.format("Room delete by id = %s failed", id)
            );
        }
        if (!roomDao.deleteAddress(maybeAddress.getId())) {
            throw new InternalDeleteException(
                    String.format("Address delete by id = %s failed", maybeAddress.getId())
            );
        }
    }
}
