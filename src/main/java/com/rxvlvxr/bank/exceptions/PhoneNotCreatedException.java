package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class PhoneNotCreatedException extends RuntimeException {
    private final ErrorResponse response;

    public PhoneNotCreatedException(ErrorResponse response) {
        this.response = response;
    }
}
