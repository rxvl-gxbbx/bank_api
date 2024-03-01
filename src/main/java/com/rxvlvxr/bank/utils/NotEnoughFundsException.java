package com.rxvlvxr.bank.utils;

import lombok.Getter;

@Getter
public class NotEnoughFundsException extends RuntimeException {
    private final String msg;

    public NotEnoughFundsException() {
        this.msg = "Недостаточно средств на счете";
    }
}
