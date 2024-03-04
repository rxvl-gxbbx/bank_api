package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class EmailNotUpdatedException extends RuntimeException {
    private final ErrorResponse response;

    public EmailNotUpdatedException(ErrorResponse response) {
        this.response = response;
    }
}
