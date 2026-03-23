package com.rent.hata.room.entity;

import com.rent.hata.room.dto.enums.RoomStatus;

import java.sql.Date;
import java.util.UUID;

public class RoomEntity {

    private UUID id;

    private UUID idOwner;

    private UUID idAddress;

    private Integer capacity;

    private Integer price;

    private String roomPictures;

    private Date added;

    private Integer views;

    private String description;

    private RoomStatus roomStatus;

    public RoomEntity() {
    }

    public RoomEntity(UUID id,
                      UUID idOwner,
                      UUID idAddress,
                      Integer capacity,
                      Integer price,
                      String roomPictures,
                      Date added,
                      Integer views,
                      String description,
                      RoomStatus roomStatus) {
        this.id = id;
        this.idOwner = idOwner;
        this.idAddress = idAddress;
        this.capacity = capacity;
        this.price = price;
        this.roomPictures = roomPictures;
        this.added = added;
        this.views = views;
        this.description = description;
        this.roomStatus = roomStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(UUID idOwner) {
        this.idOwner = idOwner;
    }

    public UUID getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(UUID idAddress) {
        this.idAddress = idAddress;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getRoomPictures() {
        return roomPictures;
    }

    public void setRoomPictures(String roomPictures) {
        this.roomPictures = roomPictures;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }
}
