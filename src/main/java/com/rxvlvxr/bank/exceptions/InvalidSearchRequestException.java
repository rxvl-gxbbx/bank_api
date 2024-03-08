package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class InvalidSearchRequestException extends RuntimeException {
    private final ErrorResponse response;

    public InvalidSearchRequestException(ErrorResponse response) {
        this.response = response;
    }
}
