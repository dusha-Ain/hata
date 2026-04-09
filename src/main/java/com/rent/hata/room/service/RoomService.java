package com.rent.hata.room.service;

import com.rent.hata.room.dao.RoomDao;
import com.rent.hata.room.dto.Address;
import com.rent.hata.room.dto.RoomDetails;
import com.rent.hata.room.dto.RoomUpdate;
import com.rent.hata.room.dto.Rooms;
import com.rent.hata.room.dto.enums.Operation;
import com.rent.hata.room.dto.enums.RoomStatus;
import com.rent.hata.room.entity.AddressEntity;
import com.rent.hata.room.entity.RoomEntity;
import com.rent.hata.room.entity.RoomsEntity;
import com.rent.hata.web.AppConfig;
import com.rent.hata.web.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomMapper mapper;
    private final RoomDao roomDao;
    private final RoomValidator roomValidator;

    public RoomService(RoomMapper mapper,
                       RoomDao roomDao,
                       RoomValidator roomValidator) {
        this.mapper = mapper;
        this.roomDao = roomDao;
        this.roomValidator = roomValidator;
    }


    @Transactional
    public RoomDetails addRoom(RoomDetails room) {
        AddressEntity address = mapper.toEntity(room.address());
        Optional<RoomEntity> maybeRoom = roomDao.getRoomByAddressWithLock(address);
        if (maybeRoom.isPresent()) {
            throw new ValidationException(String.format("Room with address = %s already exist", address.fullAddress()));
        }
        var roomEntity = mapper.toEntity(room);
        roomValidator.validateAddressCreate(roomDao.createAddress(address));
        AddressEntity savedAddress = roomValidator.checkAddressExistsInternal(
                roomDao.getAddressByAddressWithLock(address),
                address,
                Operation.CREATE
        );
        roomValidator.validateRoomCreate(roomDao.createRoom(roomEntity, savedAddress.getId()));
        var savedRoom = roomValidator.checkRoomExistsInternal(
                roomDao.getRoomByAddress(savedAddress),
                savedAddress,
                Operation.CREATE
        );
        return mapper.toDomainRoomDetails(savedRoom, savedAddress);
    }


    @Transactional
    public RoomDetails getRoomById(UUID id) {
        var room = roomValidator.checkRoomExists(roomDao.getRoomById(id), id);
        var address = roomValidator.checkAddressExists(
                roomDao.getAddressById(room.getIdAddress()),
                room.getIdAddress()
        );
        return mapper.toDomainRoomDetails(room, address);
    }


    public Rooms getAllRoomsByFilter(RoomSearchFilter filter) {
        int pageSize =  Objects.requireNonNullElse(filter.pageSize(), AppConfig.getDefaultPageSize());
        int pageNumber = Objects.requireNonNullElse(filter.pageNumber(), AppConfig.getDefaultPageNumber());
        RoomsEntity roomsEntity = roomDao.getAllRoomsByFilter(filter, pageSize, pageNumber);
        return new Rooms(roomsEntity.list.stream()
                .map(mapper::toDomainRoom).toList());
    }


    @Transactional
    public RoomUpdate updateRoomById(UUID id, RoomUpdate room) {
        var maybeRoom = roomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        var roomUpdate = mapper.toEntity(maybeRoom, room);
        roomValidator.validateRoomUpdate(roomDao.updateRoom(roomUpdate), id);
        var savedRoom = roomValidator.checkRoomExistsInternal(
                roomDao.getRoomById(id),
                id,
                Operation.UPDATE
        );
        return mapper.toDomainRoomUpdate(savedRoom);

    }

    @Transactional
    public Address updateRoomAddressById(UUID id, Address address) {
        var maybeRoom = roomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        UUID idAddress = maybeRoom.getIdAddress();
        var maybeAddress = roomValidator.checkAddressExists(
                roomDao.getAddressByIdWithLock(idAddress), idAddress
        );
        var addressUpdate = mapper.toEntity(maybeAddress, address);
        roomValidator.validateAddressUpdate(roomDao.updateRoomAddress(addressUpdate), id);
        var savedAddress = roomValidator.checkAddressExistsInternal(
                roomDao.getAddressById(idAddress),
                idAddress,
                Operation.UPDATE
        );
        return mapper.toDomainAddress(savedAddress);
    }


    @Transactional
    public void deleteRoomById(UUID id) {
        var maybeRoom = roomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        var maybeAddress = roomValidator.checkAddressExists(
                roomDao.getAddressByIdWithLock(maybeRoom.getIdAddress()), id
        );
        roomValidator.validateRoomDelete(roomDao.deleteRoom(id), id);
        roomValidator.validateAddressDelete(
                roomDao.deleteAddress(maybeAddress.getId()), maybeAddress.getId()
        );
    }

    public void increaseViews(UUID id) {
        roomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        roomValidator.validateRoomUpdate(roomDao.increaseRoomViews(id), id);
    }

    public void changeStatusRoomById(UUID id, RoomStatus status) {
        roomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        roomValidator.validateRoomUpdate(roomDao.changeRoomStatus(id, status), id);
    }
}
