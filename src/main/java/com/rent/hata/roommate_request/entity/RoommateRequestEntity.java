package com.rent.hata.roommate_request.entity;

import com.rent.hata.roommate_request.dto.enums.RequestStatus;

import java.sql.Date;
import java.util.UUID;

public class RoommateRequestEntity {

    private UUID id;

    private UUID idProfile;

    private UUID idRoom;

    private UUID idRoomOwner;

    private Integer budget;

    private Integer numberParticipants;

    private Date term;

    private String description;

    private RequestStatus status;

    public RoommateRequestEntity() {
    }

    public RoommateRequestEntity(UUID id,
                                 UUID idProfile,
                                 UUID idRoom,
                                 UUID idRoomOwner,
                                 Integer budget,
                                 Integer numberParticipants,
                                 Date term,
                                 String description,
                                 RequestStatus status) {
        this.id = id;
        this.idProfile = idProfile;
        this.idRoom = idRoom;
        this.idRoomOwner = idRoomOwner;
        this.budget = budget;
        this.numberParticipants = numberParticipants;
        this.term = term;
        this.description = description;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(UUID idProfile) {
        this.idProfile = idProfile;
    }

    public UUID getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(UUID idRoom) {
        this.idRoom = idRoom;
    }

    public UUID getIdRoomOwner() {
        return idRoomOwner;
    }

    public void setIdRoomOwner(UUID idRoomOwner) {
        this.idRoomOwner = idRoomOwner;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Integer getNumberParticipants() {
        return numberParticipants;
    }

    public void setNumberParticipants(Integer numberParticipants) {
        this.numberParticipants = numberParticipants;
    }

    public Date getTerm() {
        return term;
    }

    public void setTerm(Date term) {
        this.term = term;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
