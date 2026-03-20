package com.rent.hata.profile.dao;

import com.rent.hata.profile.dto.enums.ProfileRole;
import com.rent.hata.profile.entity.ProfileEntity;
import com.rent.hata.profile.entity.ProfilesEntity;
import com.rent.hata.web.exception.EntityToManyResultException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProfileDao {

    private final NamedParameterJdbcTemplate template;
    private final ProfileRowMapper rowMapper;

    public ProfileDao(NamedParameterJdbcTemplate template, ProfileRowMapper rowMapper) {
        this.template = template;
        this.rowMapper = rowMapper;
    }


    public Optional<ProfileEntity> getProfileByPhone(String phone) {
        String sqlLock = "SELECT * FROM profile WHERE phone = :phone";
        List<ProfileEntity> profile =
                template.query(sqlLock,
                        new MapSqlParameterSource("phone", phone),
                        rowMapper);
        if (profile.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one profile found by phone = %s", phone));
        if (profile.isEmpty())
            return Optional.empty();
        return profile.stream().findFirst();
    }


    public Optional<ProfileEntity> getProfileByPhoneWithLock(String phone) {
        String sqlLock = "SELECT * FROM profile WHERE phone = :phone FOR UPDATE";
        List<ProfileEntity> guest =
                template.query(sqlLock,
                        new MapSqlParameterSource("phone", phone),
                        rowMapper);
        if (guest.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one profile found by phone = %s", phone));
        if (guest.isEmpty())
            return Optional.empty();
        return guest.stream().findFirst();
    }


    public Boolean createProfile(ProfileEntity profile) {
        String sql = """
                INSERT INTO profile(id, first_name, last_name,
                                    patronymic, email,  password,
                                    phone, profile_picture,  registration_date,
                                     role)
                VALUES (:id, :firstName, :lastName,
                        :patronymic, :email, :password,
                        :phone, :profilePicture, :registrationDate, :role)
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID())
                .addValue("firstName", profile.getFirstName())
                .addValue("lastName", profile.getLastName())
                .addValue("patronymic", profile.getPatronymic())
                .addValue("email", profile.getEmail())
                .addValue("password", profile.getPassword())
                .addValue("phone", profile.getPhone())
                .addValue("profilePicture", profile.getProfilePicture())
                .addValue("registrationDate", LocalDate.now())
                .addValue("role", ProfileRole.USER.name());

        return template.update(sql, map) == 1;
    }

    public Optional<ProfileEntity> getById(UUID id) {
        String sqlLock = "SELECT * FROM profile WHERE id = :id";
        List<ProfileEntity> profile =
                template.query(sqlLock,
                        new MapSqlParameterSource("id", id),
                        rowMapper);
        if (profile.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one profile found by id = %s", id));
        if (profile.isEmpty())
            return Optional.empty();
        return profile.stream().findFirst();
    }


    public Optional<ProfileEntity> getByIdWithLock(UUID id) {
        String sqlLock = "SELECT * FROM profile WHERE id = :id FOR UPDATE";
        List<ProfileEntity> profile =
                template.query(sqlLock,
                        new MapSqlParameterSource("id", id),
                        rowMapper);
        if (profile.size() > 1)
            throw new EntityToManyResultException(
                    String.format("More then one profile found by id = %s", id));
        if (profile.isEmpty())
            return Optional.empty();
        return profile.stream().findFirst();
    }


    public ProfilesEntity getAll() {
        String sql = "SELECT * FROM profile";
        return new ProfilesEntity(template.query(sql, rowMapper));
    }


    public boolean delete(UUID id) {
        String sql = "DELETE FROM profile WHERE id=:id";
        return 1 == template.update(sql, new MapSqlParameterSource("id", id));
    }


    public boolean update(ProfileEntity profile) {
        String sql = """
                UPDATE profile
                SET last_name=:lastName, first_name=:firstName,
                patronymic=:patronymic, email=:email, phone=:phone,
                profile_picture=:profilePicture
                WHERE id=:id
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", profile.getId())
                .addValue("firstName", profile.getFirstName())
                .addValue("lastName", profile.getLastName())
                .addValue("patronymic", profile.getPatronymic())
                .addValue("email", profile.getEmail())
                .addValue("phone", profile.getPhone())
                .addValue("profilePicture", profile.getProfilePicture());
        return 1 == template.update(sql, map);
    }


    public boolean updatePassword(UUID id, String password) {
        String sql = """
                UPDATE profile
                SET password =:password
                WHERE id = :id
                """;
        var map = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("password", password);
        return 1 == template.update(sql, map);
    }
}
