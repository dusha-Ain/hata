package com.rent.hata.web.kafka.dto.room;

import com.rent.hata.room.service.RoomService;
import com.rent.hata.web.kafka.dto.enums.ProfileAction;
import com.rent.hata.web.kafka.dto.enums.RoomAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class RoomValidationConsumer {

    private static final Logger log = LoggerFactory.getLogger(RoomValidationConsumer.class);

    private final RoomService roomService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RoomValidationConsumer(RoomService roomService,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        this.roomService = roomService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "room-validation-requests", groupId = "main-app")
    public void handleValidationRequest(String message) {
        log.info("📨 Получено сообщение от booking-сервиса: {}", message);

        try {
            // Десериализуем JSON в объект
            RoomValidationRequest request = objectMapper.readValue(message, RoomValidationRequest.class);
            log.info("✅ Десериализован запрос: idRequest={}, idRoom={}, idRoomOwner={}, action={}",
                    request.getIdRequest(), request.getIdRoom(), request.getIdRoomOwner(), request.getAction());

            // Формируем ответ
            RoomValidationResponse response = new RoomValidationResponse();
            response.setIdRequest(request.getIdRequest());

            var action = RoomAction.valueOf(request.getAction());
            switch (action) {
                case RoomAction.GET_ROOM:
                    var room = roomService.getByIdForKafka(request.getIdRoom());
                    if (room != null) {
                        response.setExists(true);
                        response.setRoom(room);
                    } else {
                        response.setExists(false);
                        response.setErrorMessage(
                                String.format("Квартира с idRoom = %s и idRoomOwner %s не найдена",
                                        request.getIdRoom(), request.getIdRoomOwner()));
                        log.warn("⚠️ Квартира не найдена: {}, {}", request.getIdRoom(), request.getIdRoomOwner());
                    }
                    break;

                case RoomAction.CHECK_EXISTS:
                    boolean exists = roomService.existsByIdForKafka(request.getIdRoom(), request.getIdRoomOwner());
                    response.setExists(exists);
                    log.info("🔍 Квартира {}, {} существует: {}", request.getIdRoom(), request.getIdRoomOwner(), exists);
                    break;

                default:
                    response.setExists(false);
                    response.setErrorMessage("Неизвестное действие: " + request.getAction());
                    log.error("❌ Неизвестное действие: {}", request.getAction());
            }

            // Отправляем ответ обратно в Kafka
            kafkaTemplate.send("room-validation-responses", response);
            log.info("📤 Отправлен ответ на запрос {}: exists={}", request.getIdRequest(), response.isExists());

        } catch (Exception e) {
            log.error("❌ Ошибка обработки сообщения: {}", e.getMessage(), e);
        }
    }
}
