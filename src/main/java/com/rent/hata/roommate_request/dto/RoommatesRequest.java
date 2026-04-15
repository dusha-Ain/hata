package com.rent.hata.roommate_request.dto;

import java.util.List;

public class RoommatesRequest {

    public List<RoommateRequest> list;

    public RoommatesRequest(List<RoommateRequest> list) {
        this.list = list;
    }
}
