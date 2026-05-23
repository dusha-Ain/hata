package com.rent.hata.web.kafka.dto.user;

import com.rent.hata.profile.service.ProfileService;
import com.rent.hata.web.kafka.dto.enums.ProfileAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class UserValidationConsumer {


    private static final Logger log = LoggerFactory.getLogger(UserValidationConsumer.class);

    private final ProfileService profileService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserValidationConsumer(ProfileService profileService,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.profileService = profileService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "user-validation-requests", groupId = "main-app")
    public void handleValidationRequest(String message) {
        log.info("📨 Получено сообщение от booking-сервиса: {}", message);

        try {
            // Десериализуем JSON в объект
            UserValidationRequest request = objectMapper.readValue(message, UserValidationRequest.class);
            log.info("✅ Десериализован запрос: idRequest={}, idUser={}, action={}",
                    request.getIdRequest(), request.getIdUser(), request.getAction());

            // Формируем ответ
            UserValidationResponse response = new UserValidationResponse();
            response.setIdRequest(request.getIdRequest());

            var action = ProfileAction.valueOf(request.getAction());
            switch (action) {
                case ProfileAction.GET_PROFILE:
                    var profile = profileService.getByIdForKafka(request.getIdUser());
                    if (profile != null) {
                        response.setExists(true);
                        response.setProfile(profile);
                    } else {
                        response.setExists(false);
                        response.setErrorMessage("Профиль с ID " + request.getIdUser() + " не найден");
                        log.warn("⚠️ Профиль не найден: {}", request.getIdUser());
                    }
                    break;

                case ProfileAction.CHECK_EXISTS:
                    boolean exists = profileService.existsByIdForKafka(request.getIdUser());
                    response.setExists(exists);
                    log.info("🔍 Пользователь {} существует: {}", request.getIdUser(), exists);
                    break;

                default:
                    response.setExists(false);
                    response.setErrorMessage("Неизвестное действие: " + request.getAction());
                    log.error("❌ Неизвестное действие: {}", request.getAction());
            }

            // Отправляем ответ обратно в Kafka
            kafkaTemplate.send("user-validation-responses", response);
            log.info("📤 Отправлен ответ на запрос {}: exists={}", request.getIdRequest(), response.isExists());

        } catch (Exception e) {
            log.error("❌ Ошибка обработки сообщения: {}", e.getMessage(), e);
        }
    }
}