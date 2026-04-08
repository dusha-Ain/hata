--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS comfort
(
    id          UUID PRIMARY KEY,
    title       VARCHAR(20) UNIQUE NOT NULL,
    icon        VARCHAR(500),
    description VARCHAR(250)
);


--changeset create:2
CREATE TABLE IF NOT EXISTS room_comfort
(
    id_room    UUID NOT NULL,
    id_profile UUID NOT NULL,
    id_comfort UUID NOT NULL,

    FOREIGN KEY (id_room, id_profile)
        REFERENCES room (id, id_profile)
        ON DELETE CASCADE,

    CONSTRAINT FK_room_comfort_comfort
        FOREIGN KEY (id_comfort)
            REFERENCES comfort (id)
            ON DELETE CASCADE,

    PRIMARY KEY (id_room, id_profile, id_comfort)
);