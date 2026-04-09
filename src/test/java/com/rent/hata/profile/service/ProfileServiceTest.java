package com.rent.hata.profile.service;

import com.rent.hata.data.TestData;
import com.rent.hata.profile.dao.ProfileDao;
import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.profile.entity.ProfilesEntity;
import com.rent.hata.room.service.RoomMapper;
import com.rent.hata.web.exception.BadCredentialsPasswordException;
import com.rent.hata.web.exception.EntityNotFoundException;
import com.rent.hata.web.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.rent.hata.data.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileMapper mapper;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private ProfileDao profileDao;



    @Captor
    private ArgumentCaptor<ProfileEntity> profileEntityCaptor;

    private ProfileService profileService;
    private final TestData testData = new TestData();


    @BeforeEach
    void setUp() {
        profileService = new ProfileService(profileDao, mapper, roomMapper);
    }


    /**
     * =============================== addProfile() Tests ============================================
     */

    @Test
    void addProfileShouldCreateProfileWhenPhoneNotExists() {
        // given
        ProfileSecurity profileSecurity = testData.createTestProfileSecurity();
        ProfileEntity profileEntity = testData.createTestProfileEntity();
        ProfileEntity savedEntity = testData.createTestProfileEntityWithId();
        Profile expectedProfile = testData.createTestProfile();

        when(profileDao.getProfileByPhoneWithLock(TEST_PHONE)).thenReturn(Optional.empty());
        when(mapper.toEntity(profileSecurity)).thenReturn(profileEntity);
        when(profileDao.createProfile(profileEntity)).thenReturn(true);
        when(profileDao.getProfileByPhone(TEST_PHONE)).thenReturn(Optional.of(savedEntity));
        when(mapper.toDomainProfile(savedEntity)).thenReturn(expectedProfile);

        // when
        Profile result = profileService.addProfile(profileSecurity);

        // then
        assertThat(result).isEqualTo(expectedProfile);
        verify(profileDao).getProfileByPhoneWithLock(TEST_PHONE);
        verify(mapper).toEntity(profileSecurity);
        verify(profileDao).createProfile(profileEntityCaptor.capture());
        verify(profileDao).getProfileByPhone(TEST_PHONE);
        verify(mapper).toDomainProfile(savedEntity);

        assertThat(profileEntityCaptor.getValue()).isEqualTo(profileEntity);
    }


    @Test
    void addProfileShouldThrowValidationExceptionWhenPhoneAlreadyExists() {
        // given
        ProfileSecurity profileSecurity = testData.createTestProfileSecurity();
        ProfileEntity existingProfile = testData.createTestProfileEntity();

        when(profileDao.getProfileByPhoneWithLock(TEST_PHONE)).thenReturn(Optional.of(existingProfile));

        // when/then
        assertThatThrownBy(() -> profileService.addProfile(profileSecurity))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format("Profile by phone = %s already exist", TEST_PHONE));

        verify(profileDao).getProfileByPhoneWithLock(TEST_PHONE);
        verify(mapper, never()).toEntity(any());
        verify(profileDao, never()).createProfile(any());
    }


    @Test
    void addProfileShouldThrowValidationExceptionWhenCreateFails() {
        // given
        ProfileSecurity profileSecurity = testData.createTestProfileSecurity();
        ProfileEntity profileEntity = testData.createTestProfileEntity();

        when(profileDao.getProfileByPhoneWithLock(TEST_PHONE)).thenReturn(Optional.empty());
        when(mapper.toEntity(profileSecurity)).thenReturn(profileEntity);
        when(profileDao.createProfile(profileEntity)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> profileService.addProfile(profileSecurity))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Profile creation failed");

        verify(profileDao).getProfileByPhoneWithLock(TEST_PHONE);
        verify(mapper).toEntity(profileSecurity);
        verify(profileDao).createProfile(profileEntity);
        verify(profileDao, never()).getProfileByPhone(any());
    }


    @Test
    void addProfileShouldThrowValidationExceptionWhenProfileNotFoundAfterCreate() {
        // given
        ProfileSecurity profileSecurity = testData.createTestProfileSecurity();
        ProfileEntity profileEntity = testData.createTestProfileEntity();

        when(profileDao.getProfileByPhoneWithLock(TEST_PHONE)).thenReturn(Optional.empty());
        when(mapper.toEntity(profileSecurity)).thenReturn(profileEntity);
        when(profileDao.createProfile(profileEntity)).thenReturn(true);
        when(profileDao.getProfileByPhone(TEST_PHONE)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.addProfile(profileSecurity))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Profile not found after create");

        verify(profileDao).getProfileByPhoneWithLock(TEST_PHONE);
        verify(mapper).toEntity(profileSecurity);
        verify(profileDao).createProfile(profileEntity);
        verify(profileDao).getProfileByPhone(TEST_PHONE);
    }

    /**
     * =============================== getById() Tests ============================================
     */

    @Test
    void getByIdShouldReturnProfileWhenProfileExists() {
        // given
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        Profile expectedProfile = testData.createTestProfile();

        when(profileDao.getById(TEST_ID)).thenReturn(Optional.of(profileEntity));
        when(mapper.toDomainProfile(profileEntity)).thenReturn(expectedProfile);

        // when
        Profile result = profileService.getById(TEST_ID);

        // then
        assertThat(result).isEqualTo(expectedProfile);
        verify(profileDao).getById(TEST_ID);
        verify(mapper).toDomainProfile(profileEntity);
    }

    @Test
    void getByIdShouldThrowEntityNotFoundExceptionWhenProfileNotExists() {
        // given
        when(profileDao.getById(TEST_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.getById(TEST_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Profile not found by id = " + TEST_ID);

        verify(profileDao).getById(TEST_ID);
        verify(mapper, never()).toDomainProfile(any());
    }

    /**
     * =============================== getAllProfiles() Tests ============================================
     */

    @Test
    void getAllProfilesShouldReturnAllProfiles() {
        // given
        ProfileEntity entity1 = testData.createTestProfileEntityWithId();
        ProfileEntity entity2 = testData.createTestProfileEntityWithId();
        ProfileEntity entity3 = testData.createTestProfileEntityWithId();

        ProfilesEntity profilesEntity = new ProfilesEntity(List.of(entity1, entity2, entity3));

        Profile profile1 = testData.createTestProfile();
        Profile profile2 = testData.createTestProfile();
        Profile profile3 = testData.createTestProfile();

        when(profileDao.getAll()).thenReturn(profilesEntity);
        when(mapper.toDomainProfile(entity1)).thenReturn(profile1);
        when(mapper.toDomainProfile(entity2)).thenReturn(profile2);
        when(mapper.toDomainProfile(entity3)).thenReturn(profile3);

        // when
        Profiles result = profileService.getAllProfiles();

        // then
        assertThat(result.list).hasSize(3);
        assertThat(result.list).containsExactly(profile1, profile2, profile3);
        verify(profileDao).getAll();
        verify(mapper, times(3)).toDomainProfile(any(ProfileEntity.class));
    }


    @Test
    void getAllProfilesShouldReturnEmptyListWhenNoProfiles() {
        // given
        ProfilesEntity emptyList = new ProfilesEntity(List.of());
        when(profileDao.getAll()).thenReturn(emptyList);

        // when
        Profiles result = profileService.getAllProfiles();

        // then
        assertThat(result.list).isEmpty();
        verify(profileDao).getAll();
        verify(mapper, never()).toDomainProfile(any());
    }

    /**
     * =============================== deleteProfile() Tests ============================================
     */

    @Test
    void deleteProfileShouldDeleteProfileWhenProfileExists() {
        // given
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(profileEntity));
        when(profileDao.delete(TEST_ID)).thenReturn(true);

        // when
        profileService.deleteProfile(TEST_ID);

        // then
        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao).delete(TEST_ID);
    }

    @Test
    void deleteProfileShouldThrowEntityNotFoundExceptionWhenProfileNotExists() {
        // given
        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.deleteProfile(TEST_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Profile not found by id = " + TEST_ID);

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao, never()).delete(any());
    }

    @Test
    void deleteProfileShouldThrowValidationExceptionWhenDeleteFails() {
        // given
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(profileEntity));
        when(profileDao.delete(TEST_ID)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> profileService.deleteProfile(TEST_ID))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format("Profile delete by id = %s failed", TEST_ID));

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao).delete(TEST_ID);
    }

    /**
     * =============================== updateProfile() Tests ============================================
     */

    @Test
    void updateProfileShouldUpdateProfileWhenProfileExists() {
        // given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();
        ProfileEntity existingEntity = testData.createTestProfileEntityWithId();
        ProfileEntity updatedEntity = testData.createTestProfileEntityWithId();
        ProfileEntity savedEntity = testData.createTestProfileEntityWithId();
        Profile expectedProfile = testData.createTestProfile();

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(existingEntity, profileUpdate)).thenReturn(updatedEntity);
        when(profileDao.update(updatedEntity)).thenReturn(true);
        when(profileDao.getById(TEST_ID)).thenReturn(Optional.of(savedEntity));
        when(mapper.toDomainProfile(savedEntity)).thenReturn(expectedProfile);

        // when
        Profile result = profileService.updateProfile(TEST_ID, profileUpdate);

        // then
        assertThat(result).isEqualTo(expectedProfile);
        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(mapper).toEntity(existingEntity, profileUpdate);
        verify(profileDao).update(updatedEntity);
        verify(profileDao).getById(TEST_ID);
        verify(mapper).toDomainProfile(savedEntity);
    }

    @Test
    void updateProfileShouldThrowEntityNotFoundExceptionWhenProfileNotExists() {
        // given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();
        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.updateProfile(TEST_ID, profileUpdate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("Profile not found by id = %s", TEST_ID));

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(mapper, never()).toEntity(any(), any());
        verify(profileDao, never()).update(any());
    }

    @Test
    void updateProfileShouldThrowValidationExceptionWhenUpdateFails() {
        // given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();
        ProfileEntity existingEntity = testData.createTestProfileEntityWithId();
        ProfileEntity updatedEntity = testData.createTestProfileEntityWithId();

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(existingEntity, profileUpdate)).thenReturn(updatedEntity);
        when(profileDao.update(updatedEntity)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> profileService.updateProfile(TEST_ID, profileUpdate))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format("Profile update by id = %s failed", TEST_ID));

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(mapper).toEntity(existingEntity, profileUpdate);
        verify(profileDao).update(updatedEntity);
        verify(profileDao, never()).getById(any());
    }

    @Test
    void updateProfileShouldThrowValidationExceptionWhenProfileNotFoundAfterUpdate() {
        // given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();
        ProfileEntity existingEntity = testData.createTestProfileEntityWithId();
        ProfileEntity updatedEntity = testData.createTestProfileEntityWithId();

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(existingEntity, profileUpdate)).thenReturn(updatedEntity);
        when(profileDao.update(updatedEntity)).thenReturn(true);
        when(profileDao.getById(TEST_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.updateProfile(TEST_ID, profileUpdate))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format("Profile by id = %s not found after update", TEST_ID));

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(mapper).toEntity(existingEntity, profileUpdate);
        verify(profileDao).update(updatedEntity);
        verify(profileDao).getById(TEST_ID);
    }

    /**
     * =============================== updateProfilePassword() Tests ============================================
     */

    @Test
    void updateProfilePasswordShouldUpdatePasswordWhenAllDataIsValid() {
        // given
        ProfilePassword password = new ProfilePassword(
                TEST_PASSWORD,
                TEST_NEW_PASSWORD,
                TEST_NEW_PASSWORD
        );
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        profileEntity.setPassword(TEST_PASSWORD);

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(profileEntity));
        when(profileDao.updatePassword(TEST_ID, TEST_NEW_PASSWORD)).thenReturn(true);

        // when
        profileService.updateProfilePassword(TEST_ID, password);

        // then
        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao).updatePassword(TEST_ID, TEST_NEW_PASSWORD);
    }

    @Test
    void updateProfilePasswordShouldThrowEntityNotFoundExceptionWhenProfileNotExists() {
        // given
        ProfilePassword password = new ProfilePassword(
                TEST_PASSWORD,
                TEST_NEW_PASSWORD,
                TEST_NEW_PASSWORD
        );
        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.updateProfilePassword(TEST_ID, password))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("Profile not found by id = %s", TEST_ID));

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao, never()).updatePassword(any(), any());
    }


    @Test
    void updateProfilePasswordShouldThrowBadCredentialsPasswordExceptionWhenCurrentPasswordIsWrong() {
        // given
        ProfilePassword password = new ProfilePassword(
                "wrongPassword",
                TEST_NEW_PASSWORD,
                TEST_NEW_PASSWORD
        );
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        profileEntity.setPassword(TEST_PASSWORD);

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(profileEntity));

        // when/then
        assertThatThrownBy(() -> profileService.updateProfilePassword(TEST_ID, password))
                .isInstanceOf(BadCredentialsPasswordException.class)
                .hasMessageContaining("Wrong old password");

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao, never()).updatePassword(any(), any());
    }

    @Test
    void updateProfilePasswordShouldThrowValidationExceptionWhenNewPasswordAndConfirmDoNotMatch() {
        // given
        ProfilePassword password = new ProfilePassword(
                TEST_PASSWORD,
                TEST_NEW_PASSWORD,
                "differentPassword"
        );
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        profileEntity.setPassword(TEST_PASSWORD);

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(profileEntity));

        // when/then
        assertThatThrownBy(() -> profileService.updateProfilePassword(TEST_ID, password))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Passwords don't match");

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao, never()).updatePassword(any(), any());
    }

    @Test
    void updateProfilePasswordShouldThrowValidationExceptionWhenUpdateFails() {
        // given
        ProfilePassword password = new ProfilePassword(
                TEST_PASSWORD,
                TEST_NEW_PASSWORD,
                TEST_NEW_PASSWORD
        );
        ProfileEntity profileEntity = testData.createTestProfileEntityWithId();
        profileEntity.setPassword(TEST_PASSWORD);

        when(profileDao.getByIdWithLock(TEST_ID)).thenReturn(Optional.of(profileEntity));
        when(profileDao.updatePassword(TEST_ID, TEST_NEW_PASSWORD)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> profileService.updateProfilePassword(TEST_ID, password))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format("Profile password update by id = %s failed", TEST_ID));

        verify(profileDao).getByIdWithLock(TEST_ID);
        verify(profileDao).updatePassword(TEST_ID, TEST_NEW_PASSWORD);
    }
}