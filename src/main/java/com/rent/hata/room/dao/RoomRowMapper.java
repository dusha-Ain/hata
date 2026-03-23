package com.rent.hata.room.dao;

import com.rent.hata.room.dto.enums.RoomStatus;
import com.rent.hata.room.entity.RoomEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class RoomRowMapper implements RowMapper<RoomEntity> {
    @Override
    public RoomEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        RoomEntity room = new RoomEntity();
        room.setId(UUID.fromString(rs.getString("id")));
        room.setIdOwner(UUID.fromString(rs.getString("id_profile")));
        room.setIdAddress(UUID.fromString(rs.getString("id_address")));
        room.setCapacity(rs.getInt("capacity"));
        room.setPrice(rs.getInt("price"));
        room.setRoomPictures(rs.getString("room_pictures"));
        room.setAdded(rs.getDate("date_added"));
        room.setViews(rs.getInt("views"));
        room.setDescription(rs.getString("description"));
        room.setRoomStatus(RoomStatus.valueOf(rs.getString("status")));
        return room;
    }
}
