package com.rent.hata.room.entity;

import java.util.UUID;

public class AddressEntity {
    private UUID id;

    private String country;

    private String city;

    private String street;

    private String houseNumber;

    private Integer roomNumber;

    private String fullAddress;

    public AddressEntity() {
    }

    public AddressEntity(UUID id,
                         String country,
                         String city,
                         String street,
                         String houseNumber,
                         Integer roomNumber) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.roomNumber = roomNumber;
        this.fullAddress = fullAddress();
    }

    public String fullAddress() {
        return String.format("%s, %s, %s %s, кв.%d",
                country, city, street, houseNumber, roomNumber);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
