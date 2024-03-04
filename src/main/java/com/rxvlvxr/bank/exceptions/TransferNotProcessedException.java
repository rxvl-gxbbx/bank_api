package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class TransferNotProcessedException extends RuntimeException {
    private final ErrorResponse response;

    public TransferNotProcessedException(ErrorResponse response) {
        this.response = response;
    }

}
