package com.rent.hata.web.exception;

public class BadCredentialsPasswordException extends RuntimeException {
    public BadCredentialsPasswordException(String message) {
        super(message);
    }
}
