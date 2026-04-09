package com.rent.hata.profile.service;

import com.rent.hata.profile.dao.ProfileDao;
import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.room.dto.Rooms;
import com.rent.hata.room.entity.RoomsEntity;
import com.rent.hata.room.service.RoomMapper;
import com.rent.hata.web.AppConfig;
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

    public ProfileService(ProfileDao profileDao,
                          ProfileMapper mapper,
                          RoomMapper roomMapper) {
        this.profileDao = profileDao;
        this.mapper = mapper;
        this.roomMapper = roomMapper;
    }

    @Transactional
    public Profile addProfile(ProfileSecurity profile) {
        Optional<ProfileEntity> maybeGuest =
                profileDao.getProfileByPhoneWithLock(profile.phone());
        ProfileValidator.validateForCreate(profile, maybeGuest);
        var profileEntity = mapper.toEntity(profile);
        ProfileValidator.validateProfileCreate(profileDao.createProfile(profileEntity));
        ProfileEntity savedProfile = ProfileValidator.checkProfileExistsAfterCreate(
                profileDao.getProfileByPhone(profile.phone()));
        return mapper.toDomainProfile(savedProfile);
    }


    public Profile getById(UUID id) {
        ProfileEntity findProfile =
                ProfileValidator.checkProfileExists(profileDao.getById(id), id);
        return mapper.toDomainProfile(findProfile);
    }


    public Profiles getAllProfiles() {
        var profilesEntity = profileDao.getAll();
        return new Profiles(profilesEntity.list.stream()
                .map(mapper::toDomainProfile).toList());
    }


    @Transactional
    public void deleteProfile(UUID id) {
        ProfileValidator.checkProfileExists(profileDao.getByIdWithLock(id), id);
        ProfileValidator.validateProfileDelete(profileDao.delete(id), id);
    }


    @Transactional
    public Profile updateProfile(UUID id, ProfileUpdate profile) {
        ProfileEntity findProfile =
                ProfileValidator.checkProfileExists(profileDao.getByIdWithLock(id), id);
        var updateProfile = mapper.toEntity(findProfile, profile);
        ProfileValidator.validateProfileUpdate(profileDao.update(updateProfile), id);
        ProfileEntity savedProfile =
                ProfileValidator.checkProfileExistsAfterUpdate(profileDao.getById(id), id);
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
        int pageSize1 =  Objects.requireNonNullElse(pageSize, AppConfig.getDefaultPageSize());
        int pageNumber1 = Objects.requireNonNullElse(pageNumber, AppConfig.getDefaultPageNumber());
        ProfileValidator.CheckPageParam(pageSize1);
        ProfileValidator.CheckPageParam(pageNumber1);
        RoomsEntity roomsEntity = profileDao.getAllRoomsFromFavorite(id, pageSize1, pageNumber1);
        return new Rooms(roomsEntity.list.stream()
                .map(roomMapper::toDomainRoom).toList());
    }
}
