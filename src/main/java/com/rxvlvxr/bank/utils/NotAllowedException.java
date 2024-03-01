package com.rxvlvxr.bank.utils;

public class NotAllowedException extends RuntimeException {
    private final String message;

    public NotAllowedException() {
        message = "Невозможно выполнить операцию";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
