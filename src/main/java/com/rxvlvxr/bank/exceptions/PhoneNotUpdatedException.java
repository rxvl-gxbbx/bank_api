package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class PhoneNotUpdatedException extends RuntimeException {
    private final ErrorResponse response;

    public PhoneNotUpdatedException(ErrorResponse response) {
        this.response = response;
    }

}
