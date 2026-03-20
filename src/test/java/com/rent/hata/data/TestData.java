package com.rent.hata.data;

import com.rent.hata.profile.dto.Profile;
import com.rent.hata.profile.dto.ProfileSecurity;
import com.rent.hata.profile.dto.ProfileUpdate;
import com.rent.hata.profile.dto.enums.ProfileRole;
import com.rent.hata.profile.entity.ProfileEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class TestData {
    public static final UUID TEST_ID = UUID.fromString("522bd337-b7f8-4584-a0a0-a9e151c72908");

    public static final String TEST_PHONE = "+71234567890";
    public static final String TEST_PASSWORD = "oldPassword123";
    public static final String TEST_NEW_PASSWORD = "newPassword123";
    public static final String TEST_EMAIL = "test@email.com";
    public static final Date TEST_DATE = Date.valueOf(LocalDate.now());

    public ProfileSecurity createTestProfileSecurity() {
        return new ProfileSecurity(
                "Ivanov",
                "Ivan",
                "Ivanovich",
                "ivan@email.com",
                TEST_PASSWORD,
                TEST_PHONE,
                "profile.jpg"
        );
    }

    public ProfileEntity createTestProfileEntity() {
        ProfileEntity entity = new ProfileEntity();
        entity.setLastName("Ivanov");
        entity.setFirstName("Ivan");
        entity.setPatronymic("Ivanovich");
        entity.setEmail(TEST_EMAIL);
        entity.setPassword(TEST_PASSWORD);
        entity.setPhone(TEST_PHONE);
        entity.setProfilePicture("profile.jpg");
        return entity;
    }

    public ProfileEntity createTestProfileEntityWithId() {
        ProfileEntity entity = createTestProfileEntity();
        entity.setId(TEST_ID);
        entity.setRegistrationDate(Date.valueOf(LocalDate.now()));
        entity.setProfileRole(ProfileRole.USER);
        return entity;
    }

    public Profile createTestProfile() {
        return new Profile(
                TEST_ID,
                "Ivanov",
                "Ivan",
                "Ivanovich",
                TEST_EMAIL,
                "profile.jpg",
                Date.valueOf(LocalDate.now())
        );
    }

    public ProfileUpdate createTestProfileUpdate() {
        return new ProfileUpdate(
                "Petrov",
                "Petr",
                "Petrovich",
                "petr@email.com",
                "+9876543210",
                "newProfile.jpg"
        );
    }
}
