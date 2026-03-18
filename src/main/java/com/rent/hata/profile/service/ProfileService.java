package com.rent.hata.profile.service;

import com.rent.hata.profile.dao.ProfileDao;
import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.web.exception.BadCredentialsPasswordException;
import com.rent.hata.web.exception.EntityNotFoundException;
import com.rent.hata.web.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileDao profileDao;
    private final ProfileMapper mapper;

    public ProfileService(ProfileDao profileDao, ProfileMapper mapper) {
        this.profileDao = profileDao;
        this.mapper = mapper;
    }

    @Transactional
    public Profile addProfile(ProfileSecurity profile) {
        Optional<ProfileEntity> maybeGuest =
                profileDao.getProfileByPhoneWithLock(profile.phone());
        if (maybeGuest.isPresent()) {
            throw new ValidationException(String.format("Profile by phone = %s already exist", profile.phone()));
        }
        var profileEntity = mapper.toEntity(profile);
        if (!profileDao.createProfile(profileEntity)) {
            throw new ValidationException("Profile creation failed");
        }
        ProfileEntity savedProfile = profileDao.getProfileByPhone(profile.phone())
                .orElseThrow(() -> new ValidationException("Guest not found after create"));
        return mapper.toDomainProfile(savedProfile);
    }


    public Profile getById(UUID id) {
        ProfileEntity findGuest = profileDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Guest not found by id = %s", id))
                );
        return mapper.toDomainProfile(findGuest);
    }


    public Profiles getAllProfiles() {
        var profilesEntity = profileDao.getAll();
        return new Profiles(profilesEntity.list.stream()
                .map(mapper::toDomainProfile).toList());
    }


    @Transactional
    public void deleteProfile(UUID id) {
        profileDao.getByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profile not found by id = %s", id))
                );
        if (!profileDao.delete(id)) {
            throw new ValidationException(String.format("Profile delete by id = %s failed", id));
        }
    }


    @Transactional
    public Profile updateProfile(UUID id, ProfileUpdate profile) {
        ProfileEntity findProfile = profileDao.getByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profile not found by id = %s", id))
                );
        var updateProfile = mapper.toEntity(findProfile, profile);
        if (!profileDao.update(updateProfile)) {
            throw new ValidationException(String.format("Profile update by id = %s failed", id));
        }
        ProfileEntity savedProfile = profileDao.getById(id)
                .orElseThrow(() -> new ValidationException(
                        String.format("Profile by id = %s not found after update", id)));
        return mapper.toDomainProfile(savedProfile);
    }


    @Transactional
    public void updateProfilePassword(UUID id, @Valid ProfilePassword password) {
        var maybeProfile = profileDao.getByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Profile not found by id = %s", id))
                );
        if (!maybeProfile.getPassword().equals(password.currentPassword())){
            throw new BadCredentialsPasswordException("Wrong old password");
        }

        if (!password.newPassword().equals(password.confirmPassword())){
            throw new ValidationException("Passwords don't match");
        }

        if (!profileDao.updatePassword(id, password.newPassword())){
            throw new ValidationException(String.format("Profile password update by id = %s failed", id));
        };
    }
}
