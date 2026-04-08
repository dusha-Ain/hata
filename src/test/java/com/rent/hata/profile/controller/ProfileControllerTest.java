package com.rent.hata.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rent.hata.data.TestData;
import com.rent.hata.profile.dto.*;
import com.rent.hata.profile.service.ProfileService;
import com.rent.hata.web.exception.BadCredentialsPasswordException;
import com.rent.hata.web.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProfileService profileService;

    private TestData testData;
    private Profile testProfile;
    private ProfileSecurity testProfileSecurity;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        testProfile = testData.createTestProfile();  // предполагаю метод
        testProfileSecurity = testData.createTestProfileSecurity();
    }

    /**
     * ===================== addProfile() Tests ======================================
     */
    @Test
    void addProfileShouldReturnCreatedWithValidData() throws Exception {
        // Given
        when(profileService.addProfile(any(ProfileSecurity.class)))
                .thenReturn(testProfile);

        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProfileSecurity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TestData.TEST_ID.toString()))
                .andExpect(jsonPath("$.lastName").value("Ivanov"))
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.email").value(TestData.TEST_EMAIL))
                .andExpect(jsonPath("$.profilePicture").value("profile.jpg"));

        verify(profileService, times(1)).addProfile(any(ProfileSecurity.class));
    }

    @Test
    void addProfileWithInvalidDataShouldReturnBadRequest() throws Exception {
        // Given
        ProfileSecurity invalidProfile = new ProfileSecurity(
                "",                           // empty lastName
                "",                           // empty firstName
                null,                         // patronymic
                "invalid-email",              // invalid email
                "123",                        // too short password
                "123",                        // invalid phone
                null                          // profilePicture
        );
        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProfile)))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).addProfile(any());
    }


    @Test
    void addProfileWithNullDataShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).addProfile(any());
    }

    @Test
    void addProfileWithEmptyRequestBodyShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).addProfile(any());
    }

    /**
     * ===================== getProfileById()Tests =============================
     */
    @Test
    void getProfileByIdWhenExistsShouldReturnProfile() throws Exception {
        // Given
        when(profileService.getById(TestData.TEST_ID))
                .thenReturn(testProfile);

        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", TestData.TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestData.TEST_ID.toString()))
                .andExpect(jsonPath("$.lastName").value("Ivanov"))
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.patronymic").value("Ivanovich"))
                .andExpect(jsonPath("$.email").value(TestData.TEST_EMAIL))
                .andExpect(jsonPath("$.profilePicture").value("profile.jpg"))
                .andExpect(jsonPath("$.registrationDate").exists());

        verify(profileService, times(1)).getById(TestData.TEST_ID);
    }


    @Test
    void getProfileByIdWhenNotExistsShouldReturnNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(profileService.getById(nonExistentId))
                .thenThrow(new EntityNotFoundException("Profile not found by id: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(profileService, times(1)).getById(nonExistentId);
    }

    @Test
    void getProfileByIdWithInvalidIdShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/profiles/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).getById(any());
    }

    /**
     * ===================== getAll() Tests =============================
     */
    @Test
    void getAllProfilesShouldReturnListOfProfiles() throws Exception {
        // Given
        Profile profile2 = new Profile(
                UUID.randomUUID(),
                "Petrov",
                "Petr",
                "Petrovich",
                "petr@email.com",
                "petr.jpg",
                Date.valueOf(LocalDate.now())
        );

        Profiles profiles = new Profiles(Arrays.asList(testProfile, profile2));

        when(profileService.getAllProfiles()).thenReturn(profiles);

        // When & Then
        mockMvc.perform(get("/api/profiles/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.list.length()").value(2))
                .andExpect(jsonPath("$.list[0].lastName").value("Ivanov"))
                .andExpect(jsonPath("$.list[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$.list[1].lastName").value("Petrov"))
                .andExpect(jsonPath("$.list[1].firstName").value("Petr"));

        verify(profileService, times(1)).getAllProfiles();
    }

    @Test
    void getAllProfilesShouldReturnEmptyListWhenNoProfiles() throws Exception {
        // Given
        Profiles emptyProfiles = new Profiles(List.of());
        when(profileService.getAllProfiles()).thenReturn(emptyProfiles);

        // When & Then
        mockMvc.perform(get("/api/profiles/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.list.length()").value(0));

        verify(profileService, times(1)).getAllProfiles();
    }

    /**
     * ===================== updateProfile() Tests =============================
     */
    @Test
    void updateProfileWithValidDataShouldReturnUpdatedProfile() throws Exception {
        // Given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();
        Profile updatedProfile = new Profile(
                TestData.TEST_ID,
                "Petrov",
                "Petr",
                "Petrovich",
                "petr@email.com",
                "newProfile.jpg",
                TestData.TEST_DATE
        );

        when(profileService.updateProfile(eq(TestData.TEST_ID), any(ProfileUpdate.class)))
                .thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}", TestData.TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestData.TEST_ID.toString()))
                .andExpect(jsonPath("$.lastName").value("Petrov"))
                .andExpect(jsonPath("$.firstName").value("Petr"))
                .andExpect(jsonPath("$.patronymic").value("Petrovich"))
                .andExpect(jsonPath("$.email").value("petr@email.com"))
                .andExpect(jsonPath("$.profilePicture").value("newProfile.jpg"));

        verify(profileService, times(1)).updateProfile(eq(TestData.TEST_ID), any(ProfileUpdate.class));
    }

    @Test
    void updateProfileWithInvalidDataShouldReturnBadRequest() throws Exception {
        // Given
        ProfileUpdate invalidUpdate = new ProfileUpdate(
                "",                           // empty lastName
                "",                           // empty firstName
                null,                         // patronymic
                "invalid-email",              // invalid email
                "123",                        // invalid phone
                null                          // profilePicture
        );

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}", TestData.TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfileShouldReturnNotFoundWhenProfileNotFound() throws Exception {
        // Given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();
        UUID nonExistentId = UUID.randomUUID();

        when(profileService.updateProfile(eq(nonExistentId), any(ProfileUpdate.class)))
                .thenThrow(new EntityNotFoundException("Profile not found by id: " + nonExistentId));

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdate)))
                .andExpect(status().isNotFound());

        verify(profileService, times(1)).updateProfile(eq(nonExistentId), any(ProfileUpdate.class));
    }

    @Test
    void updateProfileWithInvalidIdShouldReturnBadRequest() throws Exception {
        // Given
        ProfileUpdate profileUpdate = testData.createTestProfileUpdate();

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdate)))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).updateProfile(any(), any());
    }

    /**
     * ===================== updateProfilePassword() Tests =============================
     */
    @Test
    void updateProfilePasswordWithValidDataShouldReturnOk() throws Exception {
        // Given
        ProfilePassword profilePassword = new ProfilePassword(
                TestData.TEST_PASSWORD,
                TestData.TEST_NEW_PASSWORD,
                TestData.TEST_NEW_PASSWORD
        );

        doNothing().when(profileService)
                .updateProfilePassword(eq(TestData.TEST_ID), any(ProfilePassword.class));

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}/password", TestData.TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profilePassword)))
                .andExpect(status().isOk());

        verify(profileService, times(1))
                .updateProfilePassword(eq(TestData.TEST_ID), any(ProfilePassword.class));
    }

    @Test
    void updateProfilePasswordWithInvalidDataShouldReturnBadRequest() throws Exception {
        // Given
        ProfilePassword invalidPassword = new ProfilePassword(
                "123",    // too short old password
                "456",     // too short new password
                "456"
        );

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}/password", TestData.TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPassword)))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).updateProfilePassword(any(), any());
    }

    @Test
    void updateProfilePasswordWithNullDataShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}/password", TestData.TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).updateProfilePassword(any(), any());
    }

    @Test
    void updateProfilePasswordWhenWrongOldPasswordShouldReturnBadRequest() throws Exception {
        // Given
        ProfilePassword profilePassword = new ProfilePassword(
                "wrongPassword",
                TestData.TEST_NEW_PASSWORD,
                TestData.TEST_NEW_PASSWORD
        );

        doThrow(new BadCredentialsPasswordException("Invalid old password"))
                .when(profileService)
                .updateProfilePassword(eq(TestData.TEST_ID), any(ProfilePassword.class));

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}/password", TestData.TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profilePassword)))
                .andExpect(status().isBadRequest());

        verify(profileService, times(1))
                .updateProfilePassword(eq(TestData.TEST_ID), any(ProfilePassword.class));
    }

    @Test
    void updateProfilePasswordShouldReturnNotFoundWhenProfileNotFound() throws Exception {
        // Given
        ProfilePassword profilePassword = new ProfilePassword(
                TestData.TEST_PASSWORD,
                TestData.TEST_NEW_PASSWORD,
                TestData.TEST_NEW_PASSWORD
        );
        UUID nonExistentId = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Profile not found by id: " + nonExistentId))
                .when(profileService)
                .updateProfilePassword(eq(nonExistentId), any(ProfilePassword.class));

        // When & Then
        mockMvc.perform(put("/api/profiles/update/{id}/password", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profilePassword)))
                .andExpect(status().isNotFound());

        verify(profileService, times(1))
                .updateProfilePassword(eq(nonExistentId), any(ProfilePassword.class));
    }


    /**
     * ===================== deleteProfile() Tests =============================
     */
    @Test
    void deleteProfileShouldReturnNoContentWhenExists() throws Exception {
        // Given
        doNothing().when(profileService).deleteProfile(TestData.TEST_ID);

        // When & Then
        mockMvc.perform(delete("/api/profiles/delete/{id}", TestData.TEST_ID))
                .andExpect(status().isNoContent());

        verify(profileService, times(1)).deleteProfile(TestData.TEST_ID);
    }

    @Test
    void deleteProfileShouldReturnNotFoundWhenNotExists() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Profile not found by id: " + nonExistentId))
                .when(profileService).deleteProfile(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/profiles/delete/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(profileService, times(1)).deleteProfile(nonExistentId);
    }

    @Test
    void deleteProfileWithInvalidIdShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/profiles/delete/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).deleteProfile(any());
    }
}