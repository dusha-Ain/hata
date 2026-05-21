package com.rent.hata.profile.service;

import com.rent.hata.profile.dao.ProfileDao;
import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.profile.entity.ProfilesEntity;
import com.rent.hata.room.dto.Rooms;
import com.rent.hata.room.entity.RoomsEntity;
import com.rent.hata.room.service.RoomMapper;
import com.rent.hata.roommate_request.entity.RoommateRequestEntity;
import com.rent.hata.roommate_request.service.RoommateRequestValidator;
import com.rent.hata.web.helpers.AppConfig;
import com.rent.hata.web.helpers.Pageable;
import com.rent.hata.web.kafka.ProfileEventProducer;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileDao profileDao;
    private final ProfileMapper mapper;
    private final RoomMapper roomMapper;
    private final ProfileEventProducer eventProducer;

    public ProfileService(ProfileDao profileDao,
                          ProfileMapper mapper,
                          RoomMapper roomMapper,
                          ProfileEventProducer eventProducer) {
        this.profileDao = profileDao;
        this.mapper = mapper;
        this.roomMapper = roomMapper;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public Profile addProfile(ProfileSecurity profile) {
        Optional<ProfileEntity> maybeGuest = profileDao.getProfileByPhoneWithLock(profile.phone());
        ProfileValidator.validateForCreate(profile, maybeGuest);
        var profileEntity = mapper.toEntity(profile);
        ProfileValidator.validateProfileCreate(profileDao.createProfile(profileEntity));
        ProfileEntity savedProfile = ProfileValidator.checkProfileExistsAfterCreate(
                profileDao.getProfileByPhone(profile.phone()));
        eventProducer.sendProfileCreated(mapper.toProfileKafka(savedProfile));
        return mapper.toDomainProfile(savedProfile);
    }


    public Profile getById(UUID id) {
        ProfileEntity findProfile = ProfileValidator.checkProfileExists(
                profileDao.getById(id), id);
        return mapper.toDomainProfile(findProfile);
    }


    public Profiles getAllProfilesByFilter(Pageable filter) {
        filter.setPageSize(Objects.requireNonNullElse(filter.getPageSize(), AppConfig.getDefaultPageSize()));
        filter.setPageNumber(Objects.requireNonNullElse(filter.getPageNumber(), AppConfig.getDefaultPageNumber()));
        var profilesEntity = profileDao.getAllByFilter(filter);
        return new Profiles(profilesEntity.list.stream()
                .map(mapper::toDomainProfile).toList());
    }


    @Transactional
    public void deleteProfile(UUID id) {
        ProfileValidator.checkProfileExists(profileDao.getByIdWithLock(id), id);
        ProfileValidator.validateProfileDelete(profileDao.delete(id), id);
        eventProducer.sendProfileDeleted(id);
    }


    @Transactional
    public Profile updateProfile(UUID id, ProfileUpdate profile) {
        ProfileEntity findProfile =
                ProfileValidator.checkProfileExists(profileDao.getByIdWithLock(id), id);
        var updateProfile = mapper.toEntity(findProfile, profile);
        ProfileValidator.validateProfileUpdate(profileDao.update(updateProfile), id);
        ProfileEntity savedProfile =
                ProfileValidator.checkProfileExistsAfterUpdate(profileDao.getById(id), id);
        eventProducer.sendProfileUpdated(mapper.toProfileKafka(savedProfile));
        return mapper.toDomainProfile(savedProfile);
    }


    @Transactional
    public void updateProfilePassword(UUID id, @Valid ProfilePassword password) {
        var maybeProfile =
                ProfileValidator.checkProfileExists(profileDao.getByIdWithLock(id), id);
        ProfileValidator.validatePasswordForUpdate(maybeProfile, password);
        ProfileValidator.validatePasswordUpdate(profileDao.updatePassword(id, password.newPassword()), id);
    }

    @Transactional
    public void addRoomToFavoriteProfile(IdFavorite id) {
        ProfileValidator.validateRoomFromFavoriteNotExists(
                profileDao.checkRoomFromFavoriteWithLock(id), id);
        ProfileValidator.validateAddFromFavoriteResult(
                profileDao.addRoomToFavorite(id), id);
    }

    @Transactional
    public void deleteRoomFromFavoriteProfile(IdFavorite id) {
        ProfileValidator.validateRoomFromFavoriteExists(
                profileDao.checkRoomFromFavoriteWithLock(id), id);
        ProfileValidator.validateDeleteFromFavoriteResult(
                profileDao.deleteRoomFromFavorite(id), id);
    }

    public Rooms getRoomsFromFavoriteProfile(UUID id, Integer pageSize, Integer pageNumber) {
        var filter = new Pageable();
        filter.setPageSize(Objects.requireNonNullElse(pageSize, AppConfig.getDefaultPageSize()));
        filter.setPageNumber(Objects.requireNonNullElse(pageNumber, AppConfig.getDefaultPageNumber()));
        ProfileValidator.CheckPageParams(filter);
        RoomsEntity roomsEntity = profileDao.getAllRoomsFromFavorite(id, filter);
        return new Rooms(roomsEntity.list.stream()
                .map(roomMapper::toDomainRoom).toList());
    }

    @Transactional
    public void addProfileToRequest(UUID idProfile, UUID idRequest) {
        var maybeProfile = ProfileValidator.checkProfileExists(
                profileDao.getByIdWithLock(idProfile), idProfile);
        RoommateRequestEntity findRequest =
                RoommateRequestValidator.validateRequestCheckExists(
                        profileDao.getRequestByIdWithLock(idRequest), idRequest);
        RoommateRequestValidator.checkStatusOpen(findRequest.getStatus());
        ProfileValidator.validateProfileFromRequestNotExists(
                profileDao.checkProfileFromRequestWithLock(idProfile, idRequest),
                idProfile, idRequest);
        ProfileValidator.validateAddFromRequestResult(
                profileDao.addProfileToRequest(idProfile, idRequest),
                idProfile, idRequest);
    }

    @Transactional
    public void deleteProfileFromRequest(UUID idProfile, UUID idRequest) {
        var maybeProfile = ProfileValidator.checkProfileExists(
                profileDao.getByIdWithLock(idProfile), idProfile);
        RoommateRequestEntity findRequest =
                RoommateRequestValidator.validateRequestCheckExists(
                        profileDao.getRequestByIdWithLock(idRequest), idRequest);
        RoommateRequestValidator.checkStatusOpen(findRequest.getStatus());
        ProfileValidator.validateProfileFromRequestExists(
                profileDao.checkProfileFromRequestWithLock(idProfile, idRequest),
                idProfile, idRequest);
        ProfileValidator.validateDeleteFromRequestResult(
                profileDao.deleteProfileFromRequest(idProfile, idRequest),
                idProfile, idRequest);
    }

    public Profiles getProfilesFromRequest(UUID idRequest) {
        ProfilesEntity profilesEntity = profileDao.getAllProfilesFromRequest(idRequest);
        return new Profiles(profilesEntity.list.stream()
                .map(mapper::toDomainProfile).toList());
    }

    public ProfileKafka getByIdForKafka(UUID id) {
        var profileEntity = ProfileValidator.checkProfileExists(profileDao.getById(id), id);
        return mapper.toProfileKafka(profileEntity);
    }

    public boolean existsByIdForKafka(UUID id) {
        return profileDao.existsById(id);
    }
}
