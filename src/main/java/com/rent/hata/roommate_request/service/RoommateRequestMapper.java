package com.rent.hata.roommate_request.service;

import com.rent.hata.roommate_request.dto.RoommateRequest;
import com.rent.hata.roommate_request.dto.RoommateRequestUpdate;
import com.rent.hata.roommate_request.entity.RoommateRequestEntity;
import com.rent.hata.web.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoommateRequestMapper {

    public RoommateRequestEntity toEntity(RoommateRequest request){
        if (request == null){
            throw new ValidationException("Request should be not null");
        }
        return new RoommateRequestEntity(
                request.id(),
                request.idProfile(),
                request.idRoom(),
                request.idRoomOwner(),
                request.budget(),
                request.numberParticipants(),
                request.term(),
                request.description(),
                request.status()
        );
    }

    public RoommateRequestEntity toEntity(RoommateRequestEntity requestEntity,
                                          RoommateRequestUpdate request){
        if (request == null){
            return requestEntity;
        }
        Optional.ofNullable(request.budget()).ifPresent(requestEntity::setBudget);
        Optional.ofNullable(request.numberParticipants()).ifPresent(requestEntity::setNumberParticipants);
        Optional.ofNullable(request.description()).ifPresent(requestEntity::setDescription);
        Optional.ofNullable(request.term()).ifPresent(requestEntity::setTerm);
        return requestEntity;
    }

    public RoommateRequest toDomainRequest(RoommateRequestEntity request){
        if (request == null){
            throw new ValidationException("Request should be not null");
        }
        return new RoommateRequest(
                request.getId(),
                request.getIdProfile(),
                request.getIdRoom(),
                request.getIdRoomOwner(),
                request.getBudget(),
                request.getNumberParticipants(),
                request.getTerm(),
                request.getDescription(),
                request.getStatus()
        );
    }
}
