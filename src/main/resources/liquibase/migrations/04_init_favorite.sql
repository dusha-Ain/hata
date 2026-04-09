--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS favorite
(
    id_profile    UUID NOT NULL,
    id_room       UUID NOT NULL,
    id_room_owner UUID NOT NULL,

    CONSTRAINT FK_favorite_profile
        FOREIGN KEY (id_profile)
            REFERENCES profile (id)
            ON DELETE CASCADE,

    CONSTRAINT FK_favorite_room
        FOREIGN KEY (id_room, id_room_owner)
            REFERENCES room (id, id_profile)
            ON DELETE CASCADE,

    PRIMARY KEY (id_profile, id_room, id_room_owner)
);