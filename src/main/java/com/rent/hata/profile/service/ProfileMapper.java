package com.rent.hata.profile.service;

import com.rent.hata.profile.dto.Profile;
import com.rent.hata.profile.dto.ProfileSecurity;
import com.rent.hata.profile.dto.ProfileUpdate;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.web.exception.ValidationException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProfileMapper {

    public ProfileEntity toEntity(ProfileSecurity profile) {
        if (profile == null) {
            throw new ValidationException("Profile should be not null");
        }
        var profileEntity = new ProfileEntity();
        profileEntity.setLastName(profile.lastName());
        profileEntity.setFirstName(profile.firstName());
        profileEntity.setPatronymic(profile.patronymic());
        profileEntity.setEmail(profile.email());
        profileEntity.setPassword(profile.password());
        profileEntity.setPhone(profile.phone());
        profileEntity.setProfilePicture(profile.profilePicture());
        return profileEntity;
    }

    public ProfileEntity toEntity(ProfileEntity profileEntity,
                                  ProfileUpdate profile) {
        if (profile == null) {
            return profileEntity;
        }
        Optional.ofNullable(profile.lastName()).ifPresent(profileEntity::setLastName);
        Optional.ofNullable(profile.firstName()).ifPresent(profileEntity::setFirstName);
        Optional.ofNullable(profile.patronymic()).ifPresent(profileEntity::setPatronymic);
        Optional.ofNullable(profile.email()).ifPresent(profileEntity::setEmail);
        Optional.ofNullable(profile.phone()).ifPresent(profileEntity::setPhone);
        Optional.ofNullable(profile.profilePicture()).ifPresent(profileEntity::setProfilePicture);
        return profileEntity;
    }


    public Profile toDomainProfile(ProfileEntity profile) {
        if (profile == null) {
            throw new InternalException("Profile should be not null");
        }
        return new Profile(
                profile.getId(),
                profile.getLastName(),
                profile.getFirstName(),
                profile.getPatronymic(),
                profile.getEmail(),
                profile.getProfilePicture(),
                profile.getRegistrationDate()
        );
    }
}
