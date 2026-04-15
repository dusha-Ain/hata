package com.rent.hata.profile.dao;

import com.rent.hata.data.TestData;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.room.dao.RoomRowMapper;
import com.rent.hata.roommate_request.dao.RoommateRequestRowMapper;
import com.rent.hata.web.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static com.rent.hata.data.TestData.TEST_EMAIL;
import static com.rent.hata.data.TestData.TEST_PHONE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class ProfileDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("testDB")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:liquibase/changelog-master.yml");
        registry.add("spring.liquibase.drop-first", () -> "true");
    }


    @Autowired
    private NamedParameterJdbcTemplate template;

    @Autowired
    private ProfileRowMapper rowMapper;

    @Autowired
    private RoomRowMapper roomRowMapper;

    @Autowired
    private RoommateRequestRowMapper requestRowMapper;

    private final TestData testData = new TestData();
    private ProfileDao profileDao;


    @BeforeEach
    void setUp() {
        profileDao = new ProfileDao(template, rowMapper, roomRowMapper, requestRowMapper);
    }


    /**
     * =========================== createProfile() Tests =================================
     */

    @Test
    void createProfileShouldCreateProfileWhenValidData() {
        // given
        ProfileEntity profile = testData.createTestProfileEntity();

        // when
        boolean result = profileDao.createProfile(profile);

        // then
        assertThat(result).isTrue();

        // проверяем что профиль действительно создан
        Optional<ProfileEntity> saved = profileDao.getProfileByPhone(profile.getPhone());
        assertThat(saved).isPresent();
        assertThat(saved.get().getPhone()).isEqualTo(TEST_PHONE);
        assertThat(saved.get().getEmail()).isEqualTo(TEST_EMAIL);
    }

    /**
     * =========================== getProfileByPhone() Tests =================================
     */

    @Test
    void getProfileByPhoneShouldReturnProfileWhenPhoneExists() {
        // Given
        ProfileEntity expected = testData.createTestProfileEntity();

        // When
        profileDao.createProfile(expected);
        Optional<ProfileEntity> result = profileDao.getProfileByPhone(TEST_PHONE);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo(TEST_PHONE);
    }


    @Test
    void getProfileByPhoneShouldReturnEmptyWhenPhoneNotExists() {
        // Given
        String nonExistentPhone = "+79990000000";

        // When
        Optional<ProfileEntity> result = profileDao.getProfileByPhone(nonExistentPhone);

        // Then
        assertThat(result).isEmpty();
    }


    /**
     * =========================== getProfileByPhoneWithLock() Tests =================================
     */
    @Test
    void getProfileByPhoneWithLockShouldReturnProfileWhenPhoneExists() {
        // Given
        ProfileEntity expected = testData.createTestProfileEntity();

        // When
        profileDao.createProfile(expected);
        Optional<ProfileEntity> result = profileDao.getProfileByPhoneWithLock(TEST_PHONE);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo(TEST_PHONE);
    }


    @Test
    void getProfileByPhoneWithLockShouldReturnEmptyWhenPhoneNotExists() {
        // Given
        String nonExistentPhone = "+79990000000";

        // When
        Optional<ProfileEntity> result = profileDao.getProfileByPhoneWithLock(nonExistentPhone);

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * =========================== getById() Tests =================================
     */
    @Test
    void getByIdShouldReturnProfileWhenIdExists() {
        // Given
        ProfileEntity expected = testData.createTestProfileEntity();
        profileDao.createProfile(expected);
        ProfileEntity savedProfile = profileDao.getProfileByPhone(TEST_PHONE)
                .orElseThrow(() -> new ValidationException("Profile not found after create"));

        // When
        Optional<ProfileEntity> result = profileDao.getById(savedProfile.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedProfile.getId());
        assertThat(result.get().getLastName()).isEqualTo("Ivanov");
    }

    @Test
    void getByIdShouldReturnEmptyWhenIdNotExists() {
        // When
        Optional<ProfileEntity> result = profileDao.getById(UUID.randomUUID());

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * =========================== getByIdWithLock() Tests =================================
     */
    @Test
    void getByIdWithLockShouldReturnProfileWithLockWhenIdExists() {
        // Given
        ProfileEntity expected = testData.createTestProfileEntityWithId();
        profileDao.createProfile(expected);
        ProfileEntity savedProfile = profileDao.getProfileByPhone(TEST_PHONE)
                .orElseThrow(() -> new ValidationException("Profile not found after create"));

        // When
        Optional<ProfileEntity> result = profileDao.getByIdWithLock(savedProfile.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedProfile.getId());
    }

    /**
     * =========================== getAll() Tests =================================
     */
//    @Test
//    void getAllShouldReturnAllByFilterProfiles() {
//        // Given
//        var filter = new Pageable();
//        filter.setPageSize(10);
//        filter.setPageNumber(1);
//        ProfileEntity profile1 = testData.createTestProfileEntity();
//        ProfileEntity profile2 = testData.createTestProfileEntity();
//        profile2.setPhone("+79998887766");
//        profile2.setEmail("another@email.com");
//
//        profileDao.createProfile(profile1);
//        profileDao.createProfile(profile2);
//
//        // When
//        ProfilesEntity result = profileDao.getAllByFilter(filter);
//
//        // Then
//        assertThat(result.list).hasSize(2);
//        assertThat(result.list).extracting(ProfileEntity::getPhone)
//                .containsExactlyInAnyOrder(TEST_PHONE, profile2.getPhone());
//    }
//
//
//    @Test
//    void getAllByFilterShouldReturnEmptyListWhenNoProfiles() {
//        // When
//        var filter = new Pageable();
//        filter.setPageSize(10);
//        filter.setPageNumber(1);
//        ProfilesEntity result = profileDao.getAllByFilter(filter);
//
//        // Then
//        assertThat(result.list.isEmpty());
//    }

    /**
     * =========================== delete() Tests =================================
     */
    @Test
    void deleteShouldRemoveProfile() {
        // Given
        ProfileEntity profile = testData.createTestProfileEntity();
        profileDao.createProfile(profile);
        ProfileEntity savedProfile = profileDao.getProfileByPhone(TEST_PHONE)
                .orElseThrow(() -> new ValidationException("Profile not found"));

        // When

        Boolean result = profileDao.delete(savedProfile.getId());

        // Then
        assertThat(result).isTrue();
        Optional<ProfileEntity> deleted = profileDao.getById(savedProfile.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void deleteShouldReturnFalseWhenIdNotExists() {
        // When
        Boolean result = profileDao.delete(UUID.randomUUID());

        // Then
        assertThat(result).isFalse();
    }

    /**
     * =========================== update() Tests =================================
     */
    @Test
    void updateShouldUpdateProfileFields() {
        // Given
        ProfileEntity profile = testData.createTestProfileEntity();
        profileDao.createProfile(profile);
        ProfileEntity savedProfile = profileDao.getProfileByPhone(TEST_PHONE)
                .orElseThrow(() -> new ValidationException("Profile not found after create"));
        // When
        profile.setId(savedProfile.getId());
        profile.setLastName("Petrov");
        profile.setFirstName("Petr");
        profile.setPatronymic("Petrovich");
        profile.setEmail("petr@email.com");
        profile.setPhone("+9876543210");
        profile.setProfilePicture("newProfile.jpg");

        Boolean result = profileDao.update(profile);

        // Then
        assertThat(result).isTrue();
        Optional<ProfileEntity> updated = profileDao.getProfileByPhone(profile.getPhone());
        assertThat(updated).isPresent();
        assertThat(updated.get().getLastName()).isEqualTo("Petrov");
        assertThat(updated.get().getFirstName()).isEqualTo("Petr");
        assertThat(updated.get().getPatronymic()).isEqualTo("Petrovich");
        assertThat(updated.get().getEmail()).isEqualTo("petr@email.com");
        assertThat(updated.get().getPhone()).isEqualTo("+9876543210");
        assertThat(updated.get().getProfilePicture()).isEqualTo("newProfile.jpg");
    }


    @Test
    void updateShouldReturnFalseWhenIdNotExists() {
        // Given
        ProfileEntity nonExistentProfile = testData.createTestProfileEntityWithId();

        // When
        Boolean result = profileDao.update(nonExistentProfile);

        // Then
        assertThat(result).isFalse();
    }

    /**
     * =========================== updatePassword() Tests =================================
     */


    @Test
    void updatePasswordShouldUpdateOnlyPassword() {
        // Given
        ProfileEntity profile = testData.createTestProfileEntity();
        profileDao.createProfile(profile);
        ProfileEntity savedProfile = profileDao.getProfileByPhone(TEST_PHONE)
                .orElseThrow(() -> new ValidationException("Profile not found after create"));
        String newPassword = "newEncodedPassword123";

        // When
        Boolean result = profileDao.updatePassword(savedProfile.getId(), newPassword);

        // Then
        assertThat(result).isTrue();
        Optional<ProfileEntity> updated = profileDao.getById(savedProfile.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getPassword()).isEqualTo(newPassword);
        assertThat(updated.get().getLastName()).isEqualTo(profile.getLastName());
        assertThat(updated.get().getFirstName()).isEqualTo(profile.getFirstName());
        assertThat(updated.get().getEmail()).isEqualTo(profile.getEmail());
    }


    @Test
    void updatePasswordShouldReturnFalseWhenIdNotExists() {
        // When
        Boolean result = profileDao.updatePassword(UUID.randomUUID(), "newPassword");

        // Then
        assertThat(result).isFalse();
    }
}