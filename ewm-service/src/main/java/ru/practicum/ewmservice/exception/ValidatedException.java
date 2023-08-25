package ru.practicum.ewmservice.exception;

public class ValidatedException extends RuntimeException {
    public ValidatedException(String message) {
        super(message);
    }
}