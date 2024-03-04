package com.rxvlvxr.bank.exceptions;

import lombok.Getter;

@Getter
public class NotEnoughFundsException extends RuntimeException {
    private final String message;

    public NotEnoughFundsException() {
        this.message = "Недостаточно средств на счете";
    }
}
