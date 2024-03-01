package com.rxvlvxr.bank.utils;

import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException {
    private final String msg;

    public EmailNotFoundException() {
        this.msg = "Почта не найдена";
    }
}
