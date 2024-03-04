package com.rxvlvxr.bank.exceptions;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import lombok.Getter;

@Getter
public class EmailNotCreatedException extends RuntimeException {
    private final ErrorResponse response;

    public EmailNotCreatedException(ErrorResponse response) {
        this.response = response;
    }

}
