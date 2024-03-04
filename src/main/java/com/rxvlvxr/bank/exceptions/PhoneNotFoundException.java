package com.rxvlvxr.bank.exceptions;

import lombok.Getter;

@Getter
public class PhoneNotFoundException extends RuntimeException {
    private final String message;

    public PhoneNotFoundException() {
        this.message = "Телефон не найден";
    }
}
