package com.rxvlvxr.bank.exceptions;

public class ForbiddenException extends RuntimeException {
    private final String message;

    public ForbiddenException() {
        message = "Доступ запрещен";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
