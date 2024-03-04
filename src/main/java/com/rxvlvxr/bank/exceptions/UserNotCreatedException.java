package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class UserNotCreatedException extends RuntimeException {
    private final ErrorResponse response;

    public UserNotCreatedException(ErrorResponse response) {
        this.response = response;
    }
}
