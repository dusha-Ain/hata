--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS roommate_request
(
    id                  UUID PRIMARY KEY,
    id_profile          UUID    NOT NULL,
    id_room             UUID    NOT NULL,
    id_room_owner       UUID    NOT NULL,
    budget              INTEGER NOT NULL,
    number_participants INTEGER NOT NULL,
    term                DATE,
    description         VARCHAR(250),
    status              VARCHAR(20) CHECK ( status IN ('OPEN', 'FULL', 'CLOSE')),

    CONSTRAINT FK_roommate_request_profile
        FOREIGN KEY (id_profile)
            REFERENCES profile (id)
            ON DELETE CASCADE,

    CONSTRAINT FK_roommate_request_room
        FOREIGN KEY (id_room, id_room_owner)
            REFERENCES room (id, id_profile)
            ON DELETE CASCADE
);

--changeset create:2
CREATE TABLE IF NOT EXISTS profile_roommate_request
(
    id_profile  UUID NOT NULL,
    id_roommate UUID NOT NULL,

    CONSTRAINT FK_roommate_profile
        FOREIGN KEY (id_profile)
            REFERENCES profile (id)
            ON DELETE CASCADE,

    CONSTRAINT FK_roommate_roommate_request
        FOREIGN KEY (id_roommate)
            REFERENCES roommate_request (id)
            ON DELETE CASCADE
)