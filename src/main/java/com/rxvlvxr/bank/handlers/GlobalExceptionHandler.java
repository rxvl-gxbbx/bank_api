package com.rxvlvxr.bank.handlers;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import com.rxvlvxr.bank.dtos.Response;
import com.rxvlvxr.bank.exceptions.ForbiddenException;
import com.rxvlvxr.bank.exceptions.NotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;

// ловим исключения глобально
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(DateTimeParseException e) {
        String msg = "Неверная дата. Введите дату в корректном формате. Например: yyyy-MM-dd";

        log.error("Ошибка! {}", msg);
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(msg, LocalDateTime.now()))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ForbiddenException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotAllowedException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.METHOD_NOT_ALLOWED);
    }
}
