package com.rent.hata.room.service;

import com.rent.hata.room.dto.AddressSearch;

public record RoomSearchFilter(
        Integer price,
        Integer capacity,
        AddressSearch address,
        Integer pageSize,
        Integer pageNumber
) {
}
