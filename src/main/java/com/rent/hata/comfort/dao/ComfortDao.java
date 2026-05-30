package com.rent.hata.comfort.dao;

import com.rent.hata.comfort.dto.IdComfortRoom;
import com.rent.hata.comfort.entity.ComfortEntity;
import com.rent.hata.comfort.entity.ComfortsEntity;
import com.rent.hata.comfort.service.ComfortSearchFilter;
import com.rent.hata.comfort.service.ComfortValidator;
import com.rent.hata.web.helpers.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ComfortDao {

    private final NamedParameterJdbcTemplate template;
    private final ComfortRowMapper rowMapper;

    public ComfortDao(NamedParameterJdbcTemplate template,
                      ComfortRowMapper comfortRowMapper) {
        this.template = template;
        this.rowMapper = comfortRowMapper;
    }

    public Optional<ComfortEntity> getComfortByIdWithLock(UUID id) {
        String sql = "SELECT * FROM comfort WHERE id=:id FOR UPDATE";
        List<ComfortEntity> comfort =
                template.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        return ComfortValidator.validateSearchResult(comfort, id.toString());
    }

    public Optional<ComfortEntity> getComfortById(UUID id) {
        String sql = "SELECT * FROM comfort WHERE id=:id";
        List<ComfortEntity> comfort =
                template.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        return ComfortValidator.validateSearchResult(comfort, id.toString());
    }

    public Optional<ComfortEntity> getComfortByTittleWithLock(String title) {
        String sql = "SELECT * FROM comfort WHERE title=:title FOR UPDATE";
        List<ComfortEntity> comfort =
                template.query(sql, new MapSqlParameterSource("title", title), rowMapper);
        return ComfortValidator.validateSearchResult(comfort, title);
    }

    public Optional<ComfortEntity> getComfortByTittle(String title) {
        String sql = "SELECT * FROM comfort WHERE title=:title";
        List<ComfortEntity> comfort =
                template.query(sql, new MapSqlParameterSource("title", title), rowMapper);
        return ComfortValidator.validateSearchResult(comfort, title);
    }

    public ComfortsEntity getAllComfortsByFilter(
            ComfortSearchFilter filter, Pageable pageable) {
        String sql = """
                SELECT *
                FROM comfort AS c
                WHERE  c.title = COALESCE(:title, c.title)
                LIMIT :limit OFFSET :offset
                """;
        var map = new MapSqlParameterSource()
                .addValue("title", filter.tittle())
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", (pageable.getPageNumber() - 1) * pageable.getPageSize());
        return new ComfortsEntity(template.query(sql, map, rowMapper));
    }

    public boolean createComfort(ComfortEntity comfort) {
        String sql = """
                INSERT INTO comfort(id, title, icon, description)
                VALUES(:id, :title, :icon, :desc)
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID())
                .addValue("title", comfort.getTittle())
                .addValue("icon", comfort.getIcon())
                .addValue("desc", comfort.getDescription());
        return 1 == template.update(sql, map);
    }

    public boolean updateComfort(ComfortEntity comfort) {
        String sql = """
                UPDATE comfort
                SET title=:title, icon=:icon, description=:desc
                WHERE id=:id;
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", comfort.getId())
                .addValue("title", comfort.getTittle())
                .addValue("icon", comfort.getIcon())
                .addValue("desc", comfort.getDescription());
        return 1 == template.update(sql, map);
    }

    public boolean deleteComfort(UUID id) {
        String sql = "DELETE FROM comfort WHERE id=:id";
        return 1 == template.update(sql, new MapSqlParameterSource("id", id));
    }

    public boolean checkComfortFromRoomWithLock(IdComfortRoom id) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM room_comfort
                    WHERE id_room =:idRoom
                        AND id_profile=:idProfile
                        AND id_comfort=:idComfort
                ) FOR UPDATE
                """;
        var map = new MapSqlParameterSource()
                .addValue("idRoom", id.idRoom())
                .addValue("idProfile", id.idProfile())
                .addValue("idComfort", id.idComfort());
        return Boolean.TRUE.equals(template.queryForObject(sql, map, Boolean.class));
    }

    public boolean addComfortToRoom(IdComfortRoom id) {
        String sql = """
                INSERT INTO room_comfort(id_room, id_profile, id_comfort)
                VALUES(:idRoom, :idProfile, :idComfort)
                """;
        var map = new MapSqlParameterSource()
                .addValue("idRoom", id.idRoom())
                .addValue("idProfile", id.idProfile())
                .addValue("idComfort", id.idComfort());
        return 1 == template.update(sql, map);
    }

    public boolean deleteComfortFromRoom(IdComfortRoom id) {
        String sql = """
                DELETE FROM room_comfort
                WHERE id_room=:idRoom
                  AND id_profile=:idProfile
                  AND id_comfort=:idComfort
                """;
        var map = new MapSqlParameterSource()
                .addValue("idRoom", id.idRoom())
                .addValue("idProfile", id.idProfile())
                .addValue("idComfort", id.idComfort());
        return 1 == template.update(sql, map);
    }

    public ComfortsEntity getAllComfortsFromRoom(UUID idRoom, UUID idProfile) {
        String sql = """
                SELECT c.*
                FROM comfort AS c
                JOIN room_comfort AS rc ON c.id=rc.id_comfort
                WHERE rc.id_room=:idRoom AND rc.id_profile=:idProfile
                ORDER BY c.title
                """;
        var map = new MapSqlParameterSource()
                .addValue("idRoom", idRoom)
                .addValue("idProfile", idProfile);
        return new ComfortsEntity(template.query(sql, map, rowMapper));
    }
}
