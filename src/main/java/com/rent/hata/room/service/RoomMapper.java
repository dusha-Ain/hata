package com.rent.hata.room.service;

import com.rent.hata.room.dto.Address;
import com.rent.hata.room.dto.Room;
import com.rent.hata.room.dto.RoomDetails;
import com.rent.hata.room.dto.RoomUpdate;
import com.rent.hata.room.entity.AddressEntity;
import com.rent.hata.room.entity.RoomEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoomMapper {

    private final RoomValidator roomValidator;

    public RoomMapper(RoomValidator roomValidator) {
        this.roomValidator = roomValidator;
    }

    public AddressEntity toEntity(Address address) {
        roomValidator.checkNotNull(address);
        return new AddressEntity(
                address.id(),
                address.country(),
                address.city(),
                address.street(),
                address.houseNumber(),
                address.roomNumber()
        );
    }

    public AddressEntity toEntity(AddressEntity addressEntity, Address address){
        if (address == null) {
            return addressEntity;
        }
        Optional.ofNullable(address.country()).ifPresent(addressEntity::setCountry);
        Optional.ofNullable(address.city()).ifPresent(addressEntity::setCity);
        Optional.ofNullable(address.street()).ifPresent(addressEntity::setStreet);
        Optional.ofNullable(address.houseNumber()).ifPresent(addressEntity::setHouseNumber);
        Optional.ofNullable(address.roomNumber()).ifPresent(addressEntity::setRoomNumber);
        addressEntity.setFullAddress(addressEntity.fullAddress());
        return addressEntity;
    }

    public Address toDomainAddress(AddressEntity address) {
        roomValidator.checkNotNull(address);
        return new Address(
                address.getId(),
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getRoomNumber(),
                address.fullAddress()
        );
    }

    public RoomEntity toEntity(RoomDetails room) {
        roomValidator.checkNotNull(room);
        return new RoomEntity(
                room.id(),
                room.idOwner(),
                room.address().id(),
                room.capacity(),
                room.price(),
                room.roomPictures(),
                room.added(),
                room.views(),
                room.description(),
                room.roomStatus()
        );
    }

    public RoomEntity toEntity(RoomEntity roomEntity, RoomUpdate room){
        if (room == null)
            return roomEntity;
        Optional.ofNullable(room.capacity()).ifPresent(roomEntity::setCapacity);
        Optional.ofNullable(room.price()).ifPresent(roomEntity::setPrice);
        Optional.ofNullable(room.roomPictures()).ifPresent(roomEntity::setRoomPictures);
        Optional.ofNullable(room.description()).ifPresent(roomEntity::setDescription);
        return roomEntity;
    }

    public RoomUpdate toDomainRoomUpdate(RoomEntity room){
        roomValidator.checkNotNull(room);
        return new RoomUpdate(
                room.getCapacity(),
                room.getPrice(),
                room.getRoomPictures(),
                room.getDescription()
        );
    }

    public RoomDetails toDomainRoomDetails(RoomEntity room, AddressEntity address) {
        roomValidator.checkNotNull(room);
        roomValidator.checkNotNull(address);
        return new RoomDetails(
                room.getId(),
                room.getIdOwner(),
                toDomainAddress(address),
                room.getCapacity(),
                room.getPrice(),
                room.getRoomPictures(),
                room.getAdded(),
                room.getViews(),
                room.getDescription(),
                room.getRoomStatus()
        );
    }

    public Room toDomainRoom(RoomEntity room) {
        roomValidator.checkNotNull(room);
        return new Room(
                room.getId(),
                room.getIdOwner(),
                room.getIdAddress(),
                room.getCapacity(),
                room.getPrice(),
                room.getRoomPictures(),
                room.getAdded(),
                room.getViews(),
                room.getDescription(),
                room.getRoomStatus()
        );
    }

}
