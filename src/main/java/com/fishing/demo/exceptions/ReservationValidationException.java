package com.fishing.demo.exceptions;

public class ReservationValidationException extends RuntimeException {
    public ReservationValidationException(String message) {
        super(message);
    }
}
