package com.rent.hata.profile.service;

import com.rent.hata.profile.dto.ProfilePassword;
import com.rent.hata.profile.dto.ProfileSecurity;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.web.exception.BadCredentialsPasswordException;
import com.rent.hata.web.exception.EntityNotFoundException;
import com.rent.hata.web.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProfileValidator {

    public static void validateForCreate(ProfileSecurity profile, Optional<ProfileEntity> existingProfile) {
        if (existingProfile.isPresent()) {
            throw new ValidationException(
                    String.format("Profile by phone = %s already exist", profile.phone())
            );
        }
    }

    public static void validateProfileCreate(boolean create) {
        if (!create) {
            throw new ValidationException("Profile creation failed");
        }
    }

    public static void validateProfileUpdate(boolean update, UUID id) {
        if (!update) {
            throw new ValidationException(
                    String.format("Profile update by id = %s failed", id));
        }
    }

    public static void validateProfileDelete(boolean delete, UUID id) {
        if (!delete) {
            throw new ValidationException(
                    String.format("Profile delete by id = %s failed", id));
        }
    }

    public static ProfileEntity checkProfileExistsAfterCreate(
            Optional<ProfileEntity> profile) {
        return profile.orElseThrow(() -> new ValidationException("Profile not found after create"));
    }

    public static ProfileEntity checkProfileExistsAfterUpdate(
            Optional<ProfileEntity> profile, UUID id) {
        return profile.orElseThrow(() ->
                new ValidationException(
                        String.format("Profile by id = %s not found after update", id)));
    }

    public static void validatePasswordForUpdate(
            ProfileEntity existingProfile,
            ProfilePassword password) {

        if (!existingProfile.getPassword().equals(password.currentPassword())) {
            throw new BadCredentialsPasswordException("Wrong old password");
        }
        if (!password.newPassword().equals(password.confirmPassword())) {
            throw new ValidationException("Passwords don't match");
        }
    }

    public static void validatePasswordUpdate(boolean update, UUID id){
        if (!update) {
            throw new ValidationException(
                    String.format("Profile password update by id = %s failed", id));
        }
    }


    public static ProfileEntity checkProfileExists(
            Optional<ProfileEntity> profile, UUID id) {
        return profile.orElseThrow(() -> new EntityNotFoundException(
                String.format("Profile not found by id = %s", id)
        ));
    }

}
