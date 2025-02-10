package ru.random.walk.club_service.model.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
