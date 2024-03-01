package com.rxvlvxr.bank.utils;

public class TransferNotProcessedException extends RuntimeException {
    public TransferNotProcessedException(String message) {
        super(message);
    }
}
