--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS profile
(
    id UUID PRIMARY KEY,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    patronymic VARCHAR(25),
    email VARCHAR(20) NOT NULL,
    password VARCHAR(120) NOT NULL,
    phone VARCHAR(15) UNIQUE ,
    profile_picture VARCHAR(500),
    registration_date DATE NOT NULL,
    role VARCHAR(10) CHECK ( role in ('ADMIN', 'USER', 'MODERATOR') )
)