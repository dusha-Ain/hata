package com.rent.hata.room.dto.enums;

public enum Operation {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete");

    private final String value;

    Operation(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
