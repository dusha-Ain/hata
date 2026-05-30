package com.rent.hata.web.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProfileEvent {

    private String eventType;  // PROFILE_CREATED, PROFILE_UPDATED, PROFILE_DELETED
    private UUID profileId;
    private String name;
    private String role;
    private LocalDateTime timestamp;

    public ProfileEvent() {}

    public ProfileEvent(String eventType,
                        UUID profileId,
                        String name,
                        String role,
                        LocalDateTime timestamp) {
        this.eventType = eventType;
        this.profileId = profileId;
        this.name = name;
        this.role = role;
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ProfileEvent{" +
                "eventType='" + eventType + '\'' +
                ", profileId=" + profileId +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
