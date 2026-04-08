package com.rent.hata.comfort.dao;

import com.rent.hata.comfort.entity.ComfortEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class ComfortRowMapper implements RowMapper<ComfortEntity> {
    @Override
    public ComfortEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        var comfortEntity = new ComfortEntity();
        comfortEntity.setId(UUID.fromString(rs.getString("id")));
        comfortEntity.setTittle(rs.getString("title"));
        comfortEntity.setIcon(rs.getString("icon"));
        comfortEntity.setDescription(rs.getString("description"));
        return comfortEntity;
    }
}
