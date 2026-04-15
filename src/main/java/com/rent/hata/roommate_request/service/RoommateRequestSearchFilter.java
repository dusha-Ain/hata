package com.rent.hata.roommate_request.service;

public record RoommateRequestSearchFilter(

        Integer budget,
        Integer numberParticipants,
        Integer pageSize,
        Integer pageNumber
) {

}
