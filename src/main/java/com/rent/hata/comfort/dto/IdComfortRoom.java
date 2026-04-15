package com.rent.hata.comfort.dto;

import java.util.UUID;

public record IdComfortRoom(
        UUID idComfort,
        UUID idRoom,
        UUID idProfile
) {
}
