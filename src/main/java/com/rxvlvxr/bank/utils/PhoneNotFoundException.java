package com.rxvlvxr.bank.utils;

import lombok.Getter;

@Getter
public class PhoneNotFoundException extends RuntimeException {
    private final String msg;

    public PhoneNotFoundException() {
        this.msg = "Телефон не найден";
    }
}
