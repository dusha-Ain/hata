package com.rent.hata.profile.dto;

import java.util.UUID;

public record IdFavorite(
        UUID idProfile,
        UUID idRoom,
        UUID idOwner
) {
}
