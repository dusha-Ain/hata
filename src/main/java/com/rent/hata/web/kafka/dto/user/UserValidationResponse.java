package com.rent.hata.web.kafka.dto.user;


import com.rent.hata.profile.dto.ProfileKafka;

public class UserValidationResponse {
    private String idRequest;
    private boolean exists;
    private ProfileKafka profile;
    private String errorMessage;

    public UserValidationResponse() {
    }

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public ProfileKafka getProfile() {
        return profile;
    }

    public void setProfile(ProfileKafka profile) {
        this.profile = profile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
