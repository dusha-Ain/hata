package com.rent.hata.roommate_request.dao;

import com.rent.hata.roommate_request.dto.enums.RequestStatus;
import com.rent.hata.roommate_request.entity.RoommateRequestEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class RoommateRequestRowMapper implements RowMapper<RoommateRequestEntity> {
    @Override
    public RoommateRequestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        var request = new RoommateRequestEntity();
        request.setId(UUID.fromString(rs.getString("id")));
        request.setIdProfile(UUID.fromString(rs.getString("id_profile")));
        request.setIdRoom(UUID.fromString(rs.getString("id_room")));
        request.setIdRoomOwner(UUID.fromString(rs.getString("id_room_owner")));
        request.setBudget(rs.getInt("budget"));
        request.setNumberParticipants(rs.getInt("number_participants"));
        request.setTerm(rs.getDate("term"));
        request.setDescription(rs.getString("description"));
        request.setStatus(RequestStatus.valueOf(rs.getString("status")));
        return request;
    }
}
