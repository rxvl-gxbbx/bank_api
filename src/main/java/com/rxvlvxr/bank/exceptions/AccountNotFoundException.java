package com.rxvlvxr.bank.exceptions;

public class AccountNotFoundException extends RuntimeException {
    private final String message;

    public AccountNotFoundException() {
        this.message = "Аккаунт не найден";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
