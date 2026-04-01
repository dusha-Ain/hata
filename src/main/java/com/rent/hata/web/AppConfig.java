package com.rent.hata.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${default.page.size}")
    private int defaultPageSize;

    @Value("${default.page.number}")
    private int getDefaultPageNumber;

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public int getGetDefaultPageNumber() {
        return getDefaultPageNumber;
    }
}
