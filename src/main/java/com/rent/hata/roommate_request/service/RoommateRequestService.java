package com.rent.hata.roommate_request.service;

import com.rent.hata.roommate_request.dao.RoommateRequestDao;
import com.rent.hata.roommate_request.dto.RoommateRequest;
import com.rent.hata.roommate_request.dto.RoommateRequestUpdate;
import com.rent.hata.roommate_request.dto.RoommatesRequest;
import com.rent.hata.roommate_request.dto.enums.RequestStatus;
import com.rent.hata.roommate_request.entity.RoommateRequestEntity;
import com.rent.hata.web.helpers.AppConfig;
import com.rent.hata.web.helpers.Pageable;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class RoommateRequestService {

    private final RoommateRequestDao requestDao;
    private final RoommateRequestMapper mapper;

    public RoommateRequestService(RoommateRequestDao requestDao,
                                  RoommateRequestMapper mapper) {
        this.requestDao = requestDao;
        this.mapper = mapper;
    }

    @Transactional
    public RoommateRequest createRequest(@Valid RoommateRequest request) {
        var requestEntity = mapper.toEntity(request);
        var idCreated = RoommateRequestValidator.validateCreationResult(
                requestDao.createRequest(requestEntity));
        var requestCreated =
                RoommateRequestValidator.validateRequestCheckExists(
                        requestDao.getRequestById(idCreated), idCreated);
        return mapper.toDomainRequest(requestCreated);
    }

    public RoommateRequest getRequestById(UUID id) {
        RoommateRequestEntity findRequest =
                RoommateRequestValidator.validateRequestCheckExists(requestDao.getRequestById(id), id);
        return mapper.toDomainRequest(findRequest);
    }

    public RoommatesRequest getAllRequestsByFilter(RoommateRequestSearchFilter filter) {
        var pageable = new Pageable();
        pageable.setPageSize(Objects.requireNonNullElse(filter.pageSize(), AppConfig.getDefaultPageSize()));
        pageable.setPageNumber(Objects.requireNonNullElse(filter.pageNumber(), AppConfig.getDefaultPageNumber()));
        RoommateRequestValidator.validatePageParams(pageable);
        var requestsEntity = requestDao.getAllByFilter(filter, pageable);
        return new RoommatesRequest(requestsEntity.list.stream()
                .map(mapper::toDomainRequest).toList());
    }

    @Transactional
    public RoommateRequest updateRequestById(UUID id, @Valid RoommateRequestUpdate request) {
        RoommateRequestEntity findRequest =
                RoommateRequestValidator.validateRequestCheckExists(requestDao.getRequestByIdWithLock(id), id);
        RoommateRequestValidator.checkStatusOpen(findRequest.getStatus());
        var updateRequest = mapper.toEntity(findRequest, request);
        RoommateRequestValidator.validateUpdateResult(requestDao.updateRequest(updateRequest), id);
        RoommateRequestEntity savedRequest =
                RoommateRequestValidator.validateRequestExistsAfterUpdate(requestDao.getRequestById(id), id);
        return mapper.toDomainRequest(savedRequest);
    }

    @Transactional
    public void updateStatusRequestById(UUID id, @Valid RequestStatus status) {
        RoommateRequestValidator.validateRequestCheckExists(requestDao.getRequestByIdWithLock(id), id);
        RoommateRequestValidator.validateUpdateStatusResult(requestDao.updateStatusRequest(id, status), id);
    }

    @Transactional
    public void deleteRequestById(UUID id) {
        RoommateRequestValidator.validateRequestCheckExists(requestDao.getRequestByIdWithLock(id), id);
        RoommateRequestValidator.validateDeleteResult(requestDao.deleteRequest(id), id);
    }
}
