package com.rent.hata.roommate_request.dao;

import com.rent.hata.roommate_request.dto.enums.RequestStatus;
import com.rent.hata.roommate_request.entity.RoommateRequestEntity;
import com.rent.hata.roommate_request.entity.RoommatesRequestEntity;
import com.rent.hata.roommate_request.service.RoommateRequestSearchFilter;
import com.rent.hata.roommate_request.service.RoommateRequestValidator;
import com.rent.hata.web.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoommateRequestDao {

    private final NamedParameterJdbcTemplate template;
    private final RoommateRequestRowMapper rowMapper;

    public RoommateRequestDao(NamedParameterJdbcTemplate template,
                              RoommateRequestRowMapper rowMapper) {
        this.template = template;
        this.rowMapper = rowMapper;
    }

    public Optional<RoommateRequestEntity> getRequestById(UUID id) {
        String sql = "SELECT * FROM roommate_request WHERE id = :id";
        List<RoommateRequestEntity> request =
                template.query(sql,
                        new MapSqlParameterSource("id", id),
                        rowMapper);
        return RoommateRequestValidator.validateIdSearchResult(request, id);
    }

    public Optional<RoommateRequestEntity> getRequestByIdWithLock(UUID id) {
        String sqlLock = "SELECT * FROM roommate_request WHERE id = :id FOR UPDATE";
        List<RoommateRequestEntity> request =
                template.query(sqlLock,
                        new MapSqlParameterSource("id", id),
                        rowMapper);
        return RoommateRequestValidator.validateIdSearchResult(request, id);
    }

    public UUID createRequest(RoommateRequestEntity request){
        String sql = """
                INSERT INTO roommate_request(id, id_profile, id_room,
                id_room_owner, budget, number_participants, term,
                description, status)
                VALUES(:id, :idProfile, :idRoom, :idRoomOwner, :budget,
                :participants, :term, :desc, :status)
                """;
        UUID idCreated = UUID.randomUUID();
        var map = new MapSqlParameterSource()
                .addValue("id", idCreated)
                .addValue("idProfile", request.getIdProfile())
                .addValue("idRoom", request.getIdRoom())
                .addValue("idRoomOwner", request.getIdRoomOwner())
                .addValue("budget", request.getBudget())
                .addValue("participants", request.getNumberParticipants())
                .addValue("term", request.getTerm())
                .addValue("desc", request.getDescription())
                .addValue("status", RequestStatus.OPEN.name());
        return  1 == template.update(sql, map) ? idCreated : null;
    }

    public boolean updateRequest(RoommateRequestEntity request){
        String sql = """
                UPDATE roommate_request
                SET budget=:budget, number_participants=:participants,
                term=:term, description=:desc
                WHERE id=:id
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", request.getId())
                .addValue("budget", request.getBudget())
                .addValue("participants", request.getNumberParticipants())
                .addValue("term", request.getTerm())
                .addValue("desc", request.getDescription());
        return  1 == template.update(sql, map);
    }

    public boolean updateStatusRequest(UUID id, RequestStatus status){
        String sql = "UPDATE roommate_request SET status=:status WHERE id=:id";
        var map = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status.name());
        return 1 == template.update(sql, map);
    }

    public RoommatesRequestEntity getAllByFilter(RoommateRequestSearchFilter filter,
                                                Pageable pageable) {
        String sql = """
                SELECT * FROM roommate_request AS r
                WHERE  r.budget = COALESCE(:budget, r.budget)
                AND r.number_participants = COALESCE(:participants, r.number_participants)
                LIMIT :limit OFFSET :offset
                """;
        var map = new MapSqlParameterSource()
                .addValue("budget", filter.budget())
                .addValue("participants", filter.numberParticipants())
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getPageSize() * (pageable.getPageNumber() - 1));
        return new RoommatesRequestEntity(template.query(sql, map, rowMapper));
    }

    public boolean deleteRequest(UUID id){
        String sql = "DELETE FROM roommate_request WHERE id=:id";
        return 1 == template.update(sql, new MapSqlParameterSource("id", id));
    }
}
