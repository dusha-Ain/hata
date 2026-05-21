package com.rent.hata.comfort.service;

import com.rent.hata.comfort.dao.ComfortDao;
import com.rent.hata.comfort.dto.Comfort;
import com.rent.hata.comfort.dto.Comforts;
import com.rent.hata.comfort.dto.IdComfortRoom;
import com.rent.hata.comfort.entity.ComfortEntity;
import com.rent.hata.comfort.entity.ComfortsEntity;
import com.rent.hata.web.helpers.AppConfig;
import com.rent.hata.web.helpers.Pageable;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class ComfortService {

    private final ComfortMapper mapper;
    private final ComfortDao comfortDao;

    public ComfortService(ComfortMapper mapper, ComfortDao comfortDao) {
        this.mapper = mapper;
        this.comfortDao = comfortDao;
    }


    @Transactional
    public Comfort createComfort(@Valid Comfort comfort) {
        var findComfort = comfortDao.getComfortByTittle(comfort.title());
        ComfortValidator.validateComfortNotExists(findComfort, comfort.title());
        ComfortEntity comfortEntity = mapper.toEntity(comfort);
        ComfortValidator.validateCreationResult(
                comfortDao.createComfort(comfortEntity),
                comfort.title());
        var createdComfort = ComfortValidator.validateComfortExistsAfterCreate(
                comfortDao.getComfortByTittle(comfort.title()), comfort.title());
        return mapper.toDomainComfort(createdComfort);
    }

    public Comfort getComfortById(UUID id) {
        var comfort = ComfortValidator.validateComfortCheckExists(
                comfortDao.getComfortById(id), id);
        return mapper.toDomainComfort(comfort);
    }

    public Comforts getAllComfortsByFilter(ComfortSearchFilter filter) {
        var pageable = new Pageable();
        pageable.setPageSize(Objects.requireNonNullElse(filter.pageSize(), AppConfig.getDefaultPageSize()));
        pageable.setPageNumber(Objects.requireNonNullElse(filter.pageNumber(), AppConfig.getDefaultPageNumber()));
        var comfortsEntity = comfortDao.getAllComfortsByFilter(filter, pageable);
        return new Comforts(comfortsEntity.list.stream()
                .map(mapper::toDomainComfort).toList());
    }

    @Transactional
    public Comfort updateComfortById(UUID id, @Valid Comfort comfort) {
        var findComfort = ComfortValidator.validateComfortCheckExists(
                comfortDao.getComfortByIdWithLock(id), id);

        var updateComfort = mapper.toEntity(findComfort, comfort);
        ComfortValidator.validateUpdateResult(comfortDao.updateComfort(updateComfort), id);
        ComfortEntity savedComfort = ComfortValidator.validateComfortExistsAfterUpdate(
                comfortDao.getComfortById(id), id);
        return mapper.toDomainComfort(savedComfort);
    }

    @Transactional
    public void deleteComfortById(UUID id) {
        ComfortValidator.validateComfortCheckExists(comfortDao.getComfortByIdWithLock(id), id);
        ComfortValidator.validateDeleteResult(comfortDao.deleteComfort(id), id);
    }


    @Transactional
    public void addComfortToRoom(IdComfortRoom id) {
        ComfortValidator.validateComfortFromRoomNotExists(
                comfortDao.checkComfortFromRoomWithLock(id), id);
        ComfortValidator.validateAddFromRoomResult(
                comfortDao.addComfortToRoom(id), id);
    }

    @Transactional
    public void deleteComfortFromRoom(IdComfortRoom id) {
        ComfortValidator.validateComfortFromRoomExists(
                comfortDao.checkComfortFromRoomWithLock(id), id);
        ComfortValidator.validateDeleteFromRoomResult(
                comfortDao.deleteComfortFromRoom(id), id);
    }

    public Comforts getComfortFromRoom(UUID idRoom, UUID idProfile) {
        ComfortsEntity comfortsEntity = comfortDao.getAllComfortsFromRoom(idRoom, idProfile);
        return new Comforts(comfortsEntity.list.stream()
                .map(mapper::toDomainComfort).toList());
    }
}
