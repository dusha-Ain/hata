package com.rent.hata.room.dao;

import com.rent.hata.room.entity.AddressEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class AddressRowMapper implements RowMapper<AddressEntity> {
    @Override
    public AddressEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        var address = new AddressEntity();
        address.setId(UUID.fromString(rs.getString("id")));
        address.setCountry(rs.getString("country"));
        address.setCity(rs.getString("city"));
        address.setStreet(rs.getString("street"));
        address.setHouseNumber(rs.getString("house_number"));
        address.setRoomNumber(rs.getInt("room_number"));
        address.setFullAddress(rs.getString("full_address"));
        return address;
    }
}
