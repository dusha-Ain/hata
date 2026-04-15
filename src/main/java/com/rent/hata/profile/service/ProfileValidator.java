package com.rent.hata.profile.service;

import com.rent.hata.comfort.dto.IdComfortRoom;
import com.rent.hata.profile.dto.IdFavorite;
import com.rent.hata.profile.dto.ProfilePassword;
import com.rent.hata.profile.dto.ProfileSecurity;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.web.Pageable;
import com.rent.hata.web.exception.BadCredentialsPasswordException;
import com.rent.hata.web.exception.EntityNotFoundException;
import com.rent.hata.web.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProfileValidator {

    private ProfileValidator() {}

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

    public static void validateRoomFromFavoriteExists(boolean check, IdFavorite id){
        if (!check) {
            throw new ValidationException(
                    String.format("idRoom = %s, idOwner = %s not found from favorite profile by id = %s",
                            id.idRoom(), id.idOwner(), id.idProfile()));
        }
    }

    public static void validateRoomFromFavoriteNotExists(boolean check, IdFavorite id){
        if (check) {
            throw new ValidationException(
                    String.format("idRoom = %s, idOwner = %s already exist from favorite profile by id = %s",
                            id.idRoom(), id.idOwner(), id.idProfile()));
        }
    }

    public static void validateDeleteFromFavoriteResult(boolean delete, IdFavorite id) {
        if (!delete) {
            throw new ValidationException(
                    String.format("idRoom = %s, idOwner = %s delete from favorite failed by profile id = %s",
                            id.idRoom(), id.idOwner(), id.idProfile()));
        }
    }

    public static void validateAddFromFavoriteResult(boolean add, IdFavorite id) {
        if (!add) {
            throw new ValidationException(
                    String.format("idRoom = %s, idOwner = %s add from favorite failed by profile id = %s",
                            id.idRoom(), id.idOwner(), id.idProfile()));
        }
    }

    public static void CheckPageParams(Pageable pageable){
        if (pageable.getPageSize() < 0 || pageable.getPageNumber() < 0) {
            throw new ValidationException("Page param must be positive");
        }
    }

    public static void validateProfileFromRequestExists(boolean check, UUID idProfile, UUID idRequest){
        if (!check) {
            throw new ValidationException(
                    String.format("Profile = %s not found from request by id = %s", idProfile, idRequest));
        }
    }

    public static void validateProfileFromRequestNotExists(boolean check, UUID idProfile, UUID idRequest) {
        if (check) {
            throw new ValidationException(
                    String.format("Profile = %s already exists from request by id = %s", idProfile, idRequest));
        }
    }

    public static void validateAddFromRequestResult(boolean add, UUID idProfile, UUID idRequest) {
        if (!add){
            throw new ValidationException(
                    String.format("Profile = %s add from request failed by id = %s", idProfile, idRequest));
        }
    }

    public static void validateDeleteFromRequestResult(boolean delete, UUID idProfile, UUID idRequest) {
        if (!delete) {
            throw new ValidationException(
                    String.format("Profile = %s delete from request failed by id = %s", idProfile, idRequest));
        }
    }
}
