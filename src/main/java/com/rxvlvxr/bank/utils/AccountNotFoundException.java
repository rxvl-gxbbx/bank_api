package com.rxvlvxr.bank.utils;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private final String msg;

    public AccountNotFoundException() {
        this.msg = "Аккаунт не найден";
    }
}
