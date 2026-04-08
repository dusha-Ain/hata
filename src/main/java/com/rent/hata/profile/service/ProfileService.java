package com.rent.hata.profile.service;

import com.rent.hata.profile.dao.ProfileDao;
import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.entity.ProfileEntity;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileDao profileDao;
    private final ProfileMapper mapper;

    public ProfileService(ProfileDao profileDao,
                          ProfileMapper mapper) {
        this.profileDao = profileDao;
        this.mapper = mapper;
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
}
