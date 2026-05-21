package com.rent.hata.roommate_request.service;

import com.rent.hata.roommate_request.dto.enums.RequestStatus;
import com.rent.hata.roommate_request.entity.RoommateRequestEntity;
import com.rent.hata.web.helpers.Pageable;
import com.rent.hata.web.exception.EntityNotFoundInternalException;
import com.rent.hata.web.exception.EntityToManyResultException;
import com.rent.hata.web.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoommateRequestValidator {

    private RoommateRequestValidator() {}

    public static void checkStatusOpen(RequestStatus status){
        if (status != RequestStatus.OPEN) {
            throw new ValidationException(
                    String.format("Cannot perform an operation with this status %s", status.name()));
        }
    }

    public static void validatePageParams(Pageable pageable){
        if (pageable.getPageNumber() <= 0 || pageable.getPageSize() <= 0){
            throw new ValidationException("Page param cannot be negative");
        }
    }
    public static RoommateRequestEntity validateRequestExistsAfterUpdate(
            Optional<RoommateRequestEntity> request, UUID id) {
        return request.orElseThrow(() -> new EntityNotFoundInternalException(
                String.format("Request not found after update by id = %s", id)));
    }

    public static RoommateRequestEntity validateRequestCheckExists(
            Optional<RoommateRequestEntity> request, UUID id) {
        return request.orElseThrow(() -> new ValidationException(
                String.format("Request not found by id = %s", id)));
    }

    public static UUID validateCreationResult(UUID id) {
        if (id == null) {
            throw new ValidationException("Request creation failed");
        }
        return id;
    }

    public static void validateUpdateResult(boolean updateResult, UUID id) {
        if (!updateResult) {
            throw new ValidationException(
                    String.format("Request update failed by id = %s", id));
        }
    }

    public static void validateUpdateStatusResult(boolean updateResult, UUID id) {
        if (!updateResult) {
            throw new ValidationException(
                    String.format("Request update status failed by id = %s", id));
        }
    }

    public static void validateDeleteResult(boolean deleteResult, UUID id) {
        if (!deleteResult) {
            throw new ValidationException(
                    String.format("Request delete failed by id = %s", id));
        }
    }

    public static Optional<RoommateRequestEntity> validateIdSearchResult(
            List<RoommateRequestEntity> request, UUID id) {
        if (request.size() > 1) {
            throw new EntityToManyResultException(
                    String.format("More than one request found by id = %s", id));
        }
        if (request.isEmpty()) {
            return Optional.empty();
        }
        return request.stream().findFirst();
    }
}
