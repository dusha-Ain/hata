--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS address
(
    id           UUID PRIMARY KEY,
    country      VARCHAR(25)  NOT NULL,
    city         VARCHAR(25)  NOT NULL,
    street       VARCHAR(25)  NOT NULL,
    house_number VARCHAR(10)  NOT NULL,
    room_number  INTEGER      NOT NULL CHECK ( room_number >= 0 ),
    full_address VARCHAR(100) NOT NULL
);

--changeset create:2
CREATE TABLE IF NOT EXISTS room
(
    id            UUID,
    id_profile    UUID,
    id_address    UUID,
    capacity      INTEGER NOT NULL,
    price         INTEGER CHECK ( price >= 0 ),
    room_pictures VARCHAR(500),
    date_added    DATE    NOT NULL,
    views         INTEGER NOT NULL check (views >= 0 ),
    description   VARCHAR(500),
    status        VARCHAR(10) CHECK ( status in ('FREE', 'RENTED', 'BOOKED', 'UNAVAILABLE') ),
    PRIMARY KEY (id, id_profile),
    CONSTRAINT FK_room_profile
        FOREIGN KEY (id_profile)
            REFERENCES profile (id)
            ON DELETE CASCADE
);


--changeset create:3
ALTER TABLE room
    ADD CONSTRAINT FK_room_address
        FOREIGN KEY (id_address)
            REFERENCES address (id)
            ON DELETE CASCADE;
