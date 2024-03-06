package com.rxvlvxr.bank.exceptions;

import lombok.Getter;

@Getter
public class NotEnoughFundsException extends RuntimeException {
    private final String message;

    public NotEnoughFundsException(double amount) {
        this.message = "Недостаточно средств на счете. Текущий баланс: " + amount;
    }
}
