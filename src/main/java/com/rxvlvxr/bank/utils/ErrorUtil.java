package com.rxvlvxr.bank.utils;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import com.rxvlvxr.bank.dtos.Response;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorUtil {
    public static ErrorResponse getResponse(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        ErrorResponse response = new ErrorResponse();

        for (FieldError error : errors)
            response.getErrors().add(new Response(error.getField() + " - " + error.getDefaultMessage(), LocalDateTime.now()));

        return response;
    }
}
