package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class InvalidLoginRequestException extends RuntimeException {
    private final ErrorResponse response;

    public InvalidLoginRequestException(ErrorResponse response) {
        this.response = response;
    }

}
