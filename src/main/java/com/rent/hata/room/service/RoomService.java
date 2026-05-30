package com.rent.hata.room.service;

import com.rent.hata.room.dao.RoomDao;
import com.rent.hata.room.dto.*;
import com.rent.hata.room.dto.enums.Operation;
import com.rent.hata.room.dto.enums.RoomStatus;
import com.rent.hata.room.entity.AddressEntity;
import com.rent.hata.room.entity.RoomEntity;
import com.rent.hata.room.entity.RoomsEntity;
import com.rent.hata.web.exception.ValidationException;
import com.rent.hata.web.helpers.AppConfig;
import com.rent.hata.web.helpers.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomMapper mapper;
    private final RoomDao roomDao;

    public RoomService(RoomMapper mapper,
                       RoomDao roomDao) {
        this.mapper = mapper;
        this.roomDao = roomDao;
    }


    @Transactional
    public RoomDetails addRoom(RoomDetails room) {
        AddressEntity address = mapper.toEntity(room.address());
        Optional<RoomEntity> maybeRoom = roomDao.getRoomByAddressWithLock(address);
        if (maybeRoom.isPresent()) {
            throw new ValidationException(
                    String.format("Room with address = %s already exist", address.fullAddress()));
        }
        var roomEntity = mapper.toEntity(room);
        RoomValidator.validateAddressCreate(roomDao.createAddress(address));
        AddressEntity savedAddress = RoomValidator.checkAddressExistsInternal(
                roomDao.getAddressByAddressWithLock(address),
                address,
                Operation.CREATE
        );
        RoomValidator.validateRoomCreate(roomDao.createRoom(roomEntity, savedAddress.getId()));
        var savedRoom = RoomValidator.checkRoomExistsInternal(
                roomDao.getRoomByAddress(savedAddress),
                savedAddress,
                Operation.CREATE
        );
        return mapper.toDomainRoomDetails(savedRoom, savedAddress);
    }


    @Transactional
    public RoomDetails getRoomById(UUID id) {
        var room = RoomValidator.checkRoomExists(roomDao.getRoomById(id), id);
        var address = RoomValidator.checkAddressExists(
                roomDao.getAddressById(room.getIdAddress()),
                room.getIdAddress()
        );
        return mapper.toDomainRoomDetails(room, address);
    }

    public Rooms getAllRoomsByFilter(RoomSearchFilter filter) {
        var pageable = new Pageable();
        pageable.setPageSize(Objects.requireNonNullElse(filter.pageSize(), AppConfig.getDefaultPageSize()));
        pageable.setPageNumber(Objects.requireNonNullElse(filter.pageNumber(), AppConfig.getDefaultPageNumber()));
        RoomsEntity roomsEntity = roomDao.getAllRoomsByFilter(filter, pageable);
        return new Rooms(roomsEntity.list.stream()
                .map(mapper::toDomainRoom).toList());
    }


    @Transactional
    public RoomUpdate updateRoomById(UUID id, RoomUpdate room) {
        var maybeRoom = RoomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        var roomUpdate = mapper.toEntity(maybeRoom, room);
        RoomValidator.validateRoomUpdate(roomDao.updateRoom(roomUpdate), id);
        var savedRoom = RoomValidator.checkRoomExistsInternal(
                roomDao.getRoomById(id),
                id,
                Operation.UPDATE
        );
        return mapper.toDomainRoomUpdate(savedRoom);

    }

    @Transactional
    public Address updateRoomAddressById(UUID id, Address address) {
        var maybeRoom = RoomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        UUID idAddress = maybeRoom.getIdAddress();
        var maybeAddress = RoomValidator.checkAddressExists(
                roomDao.getAddressByIdWithLock(idAddress), idAddress
        );
        var addressUpdate = mapper.toEntity(maybeAddress, address);
        RoomValidator.validateAddressUpdate(roomDao.updateRoomAddress(addressUpdate), id);
        var savedAddress = RoomValidator.checkAddressExistsInternal(
                roomDao.getAddressById(idAddress),
                idAddress,
                Operation.UPDATE
        );
        return mapper.toDomainAddress(savedAddress);
    }


    @Transactional
    public void deleteRoomById(UUID id) {
        var maybeRoom = RoomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        var maybeAddress = RoomValidator.checkAddressExists(
                roomDao.getAddressByIdWithLock(maybeRoom.getIdAddress()), id
        );
        RoomValidator.validateRoomDelete(roomDao.deleteRoom(id), id);
        RoomValidator.validateAddressDelete(
                roomDao.deleteAddress(maybeAddress.getId()), maybeAddress.getId()
        );
    }

    public void increaseViews(UUID id) {
        RoomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        RoomValidator.validateRoomUpdate(roomDao.increaseRoomViews(id), id);
    }

    public void changeStatusRoomById(UUID id, RoomStatus status) {
        RoomValidator.checkRoomExists(roomDao.getRoomByIdWithLock(id), id);
        RoomValidator.validateRoomUpdate(roomDao.changeRoomStatus(id, status), id);
    }

    public RoomKafka getByIdForKafka(UUID idRoom) {
        var roomEntity = RoomValidator.checkRoomExists(roomDao.getRoomById(idRoom), idRoom);
        return mapper.toRoomKafka(roomEntity);
    }

    public boolean existsByIdForKafka(UUID idRoom, UUID idRoomOwner){
        return roomDao.existsById(idRoom, idRoomOwner);
    }
}
