package com.rent.hata.comfort.entity;

import java.util.UUID;

public class ComfortEntity {

    private UUID id;

    private String title;

    private String icon;

    private String description;

    public ComfortEntity() {
    }

    public ComfortEntity(UUID id,
                         String title,
                         String icon,
                         String description) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTittle() {
        return title;
    }

    public void setTittle(String tittle) {
        this.title = tittle;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
