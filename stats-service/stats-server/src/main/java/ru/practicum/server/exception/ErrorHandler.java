package ru.practicum.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse invalidParameterException(final InvalidParameterException e) {
        log.warn("InvalidParameterException, server status: '{}' text message: '{}'", HttpStatus.BAD_REQUEST, e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}