package com.rxvlvxr.bank.exceptions;

import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException {
    private final String message;

    public EmailNotFoundException() {
        this.message = "Почта не найдена";
    }
}
