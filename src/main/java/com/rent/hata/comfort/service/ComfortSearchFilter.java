package com.rent.hata.comfort.service;


public record ComfortSearchFilter(
        String tittle,
        Integer pageSize,
        Integer pageNumber
) {
}
