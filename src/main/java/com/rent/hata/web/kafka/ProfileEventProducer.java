package com.rent.hata.web.kafka;


import com.rent.hata.profile.dto.ProfileKafka;
import com.rent.hata.web.kafka.dto.ProfileEvent;
import com.rent.hata.web.kafka.dto.enums.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProfileEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ProfileEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public ProfileEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProfileCreated(ProfileKafka profile) {
        ProfileEvent event = new ProfileEvent(
                ProfileEventType.PROFILE_CREATED.name(),
                UUID.fromString(profile.getId()),
                profile.getName(),
                profile.getRole(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("profile-events", event);
        log.info("Отправлено событие о создании профиля: {}", event);
    }

    public void sendProfileUpdated(ProfileKafka profile) {
        ProfileEvent event = new ProfileEvent(
                ProfileEventType.PROFILE_UPDATED.name(),
                UUID.fromString(profile.getId()),
                profile.getName(),
                profile.getRole(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("profile-events", event);
        log.info("Отправлено событие об обновлении профиля: {}", event);
    }

    public void sendProfileDeleted(UUID idProfile) {
        ProfileEvent event = new ProfileEvent(
                ProfileEventType.PROFILE_DELETED.name(),
                idProfile,
                null,
                null,
                LocalDateTime.now()
        );
        kafkaTemplate.send("profile-events", event);
        log.info("Отправлено событие об удалении профиля: {}", event);
    }
}