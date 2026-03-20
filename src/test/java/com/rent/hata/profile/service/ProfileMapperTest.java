package com.rent.hata.profile.service;

import com.rent.hata.profile.dto.Profile;
import com.rent.hata.profile.dto.ProfileSecurity;
import com.rent.hata.profile.dto.ProfileUpdate;
import com.rent.hata.profile.entity.ProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static com.rent.hata.data.TestData.TEST_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProfileMapperTest {

    private ProfileMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProfileMapper();
    }


    @Test
    void toEntityShouldMapAllFieldsWhenProfileSecurityProvided() {
        //given
        ProfileSecurity profileSecurity = new ProfileSecurity(
                "Ivanov",
                "Ivan",
                "Ivanovich",
                "ivan@email.com",
                "password123",
                "+1234567890",
                "profile.jpg"
        );

        //when
        ProfileEntity result = mapper.toEntity(profileSecurity);

        //then
        assertThat(result.getLastName()).isEqualTo("Ivanov");
        assertThat(result.getFirstName()).isEqualTo("Ivan");
        assertThat(result.getPatronymic()).isEqualTo("Ivanovich");
        assertThat(result.getEmail()).isEqualTo("ivan@email.com");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.getPhone()).isEqualTo("+1234567890");
        assertThat(result.getProfilePicture()).isEqualTo("profile.jpg");

    }


    @Test
    void toEntityShouldMapAllFieldsWhenProfileUpdateHasAllFields() {
        // given
        ProfileEntity existingEntity = new ProfileEntity();
        existingEntity.setId(TEST_ID);
        existingEntity.setLastName("OldLastName");
        existingEntity.setFirstName("OldFirstName");

        ProfileUpdate profileUpdate = new ProfileUpdate(
                "NewLastName",
                "NewFirstName",
                "NewPatronymic",
                "new@email.com",
                "+9876543210",
                "newProfile.jpg"
        );

        // when
        ProfileEntity result = mapper.toEntity(existingEntity, profileUpdate);

        // then
        assertThat(result.getId()).isEqualTo(TEST_ID); // ID должен сохраниться
        assertThat(result.getLastName()).isEqualTo("NewLastName");
        assertThat(result.getFirstName()).isEqualTo("NewFirstName");
        assertThat(result.getPatronymic()).isEqualTo("NewPatronymic");
        assertThat(result.getEmail()).isEqualTo("new@email.com");
        assertThat(result.getPhone()).isEqualTo("+9876543210");
        assertThat(result.getProfilePicture()).isEqualTo("newProfile.jpg");
        // Пароль не должен меняться при этом обновлении
        assertThat(result.getPassword()).isNull();
    }


    @Test
    void toEntityShouldIgnoreNullFieldsWhenProfileUpdateHasNulls() {
        // given
        ProfileEntity existingEntity = new ProfileEntity();
        existingEntity.setId(TEST_ID);
        existingEntity.setLastName("ExistingLastName");
        existingEntity.setFirstName("ExistingFirstName");
        existingEntity.setPatronymic("ExistingPatronymic");
        existingEntity.setEmail("existing@email.com");
        existingEntity.setPhone("+1111111111");
        existingEntity.setProfilePicture("existing.jpg");

        ProfileUpdate profileUpdate = new ProfileUpdate(
                null,  // lastName - null
                "UpdatedFirstName",  // firstName - обновляем
                null,  // patronymic - null
                null,  // email - null
                null,  // phone - null
                null   // profilePicture - null
        );

        // when
        ProfileEntity result = mapper.toEntity(existingEntity, profileUpdate);

        // then
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getLastName()).isEqualTo("ExistingLastName");
        assertThat(result.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(result.getPatronymic()).isEqualTo("ExistingPatronymic");
        assertThat(result.getEmail()).isEqualTo("existing@email.com");
        assertThat(result.getPhone()).isEqualTo("+1111111111");
        assertThat(result.getProfilePicture()).isEqualTo("existing.jpg");
    }


    @Test
    void toEntityShouldHandleEmptyOptionalWhenAllFieldsAreNull() {
        // given
        ProfileEntity existingEntity = new ProfileEntity();
        existingEntity.setId(TEST_ID);
        existingEntity.setLastName("ExistingLastName");
        existingEntity.setFirstName("ExistingFirstName");

        ProfileUpdate profileUpdate = new ProfileUpdate(
                null, null, null, null, null, null
        );

        // when
        ProfileEntity result = mapper.toEntity(existingEntity, profileUpdate);

        // then
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getLastName()).isEqualTo("ExistingLastName");
        assertThat(result.getFirstName()).isEqualTo("ExistingFirstName");
        assertThat(result.getPatronymic()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getPhone()).isNull();
        assertThat(result.getProfilePicture()).isNull();
    }


    @Test
    void toEntityShouldHandleNullInputWhenProfileUpdateIsNull() {
        // given
        ProfileEntity existingEntity = new ProfileEntity();
        existingEntity.setId(TEST_ID);
        existingEntity.setLastName("ExistingLastName");

        // when
        ProfileEntity result = mapper.toEntity(existingEntity, null);

        // then
        assertThat(result).isSameAs(existingEntity);
        assertThat(result.getLastName()).isEqualTo("ExistingLastName");
    }


    @Test
    void toDomainProfileShouldMapAllFieldsWhenProfileEntityProvided() {
        // given
        Date registrationDate = Date.valueOf(LocalDate.now());

        ProfileEntity entity = new ProfileEntity();
        entity.setId(TEST_ID);
        entity.setLastName("Petrov");
        entity.setFirstName("Petr");
        entity.setPatronymic("Petrovich");
        entity.setEmail("petr@email.com");
        entity.setProfilePicture("avatar.jpg");
        entity.setRegistrationDate(registrationDate);

        // when
        Profile result = mapper.toDomainProfile(entity);

        // then
        assertThat(result.id()).isEqualTo(TEST_ID);
        assertThat(result.lastName()).isEqualTo("Petrov");
        assertThat(result.firstName()).isEqualTo("Petr");
        assertThat(result.patronymic()).isEqualTo("Petrovich");
        assertThat(result.email()).isEqualTo("petr@email.com");
        assertThat(result.profilePicture()).isEqualTo("avatar.jpg");
        assertThat(result.registrationDate()).isEqualTo(registrationDate);
    }


    @Test
    void toDomainProfileShouldHandleNullFieldsWhenProfileEntityHasNulls() {
        // given
        ProfileEntity entity = new ProfileEntity();
        entity.setId(TEST_ID);

        // when
        Profile result = mapper.toDomainProfile(entity);

        // then
        assertThat(result.id()).isEqualTo(TEST_ID);
        assertThat(result.lastName()).isNull();
        assertThat(result.firstName()).isNull();
        assertThat(result.patronymic()).isNull();
        assertThat(result.email()).isNull();
        assertThat(result.profilePicture()).isNull();
        assertThat(result.registrationDate()).isNull();
    }
}