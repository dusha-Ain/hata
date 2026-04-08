package com.rent.hata.comfort.service;

import com.rent.hata.comfort.dao.ComfortDao;
import com.rent.hata.comfort.dto.Comfort;
import com.rent.hata.comfort.dto.Comforts;
import com.rent.hata.comfort.entity.ComfortEntity;
import com.rent.hata.comfort.entity.ComfortsEntity;
import com.rent.hata.web.AppConfig;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ComfortService {

    private final ComfortMapper mapper;
    private final ComfortDao comfortDao;
    private final AppConfig appConfig;

    public ComfortService(ComfortMapper mapper, ComfortDao comfortDao, AppConfig appConfig) {
        this.mapper = mapper;
        this.comfortDao = comfortDao;
        this.appConfig = appConfig;
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
        int pageSize = filter.pageSize() != null ? filter.pageSize() : appConfig.getDefaultPageSize();
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : appConfig.getGetDefaultPageNumber();
        var comfortsEntity = comfortDao.getAllComfortsByFilter(filter, pageSize, pageNumber);
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
    public void addComfortToRoom(UUID idComfort, UUID idRoom, UUID idProfile) {
        ComfortValidator.validateComfortFromRoomNotExists(
                comfortDao.checkComfortFromRoomWithLock(idComfort, idRoom, idProfile), idComfort, idRoom);
        ComfortValidator.validateAddFromRoomResult(
                comfortDao.addComfortToRoom(idComfort, idRoom, idProfile), idComfort, idRoom);
    }

    @Transactional
    public void deleteComfortFromRoom(UUID idComfort, UUID idRoom, UUID idProfile) {
        ComfortValidator.validateComfortFromRoomExists(
                comfortDao.checkComfortFromRoomWithLock(idComfort, idRoom, idProfile), idComfort, idRoom);
        ComfortValidator.validateDeleteFromRoomResult(
                comfortDao.deleteComfortFromRoom(idComfort, idRoom, idProfile), idComfort, idProfile);
    }

    public Comforts getComfortFromRoom(UUID idRoom, UUID idProfile) {
        ComfortsEntity comfortsEntity = comfortDao.getAllComfortsFromRoom(idRoom, idProfile);
        return new Comforts(comfortsEntity.list.stream()
                .map(mapper::toDomainComfort).toList());
    }
}
