package com.rxvlvxr.bank.handlers;

import com.rxvlvxr.bank.utils.ErrorResponse;
import com.rxvlvxr.bank.utils.ForbiddenException;
import com.rxvlvxr.bank.utils.NotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

// ловим исключения глобально
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(DateTimeParseException e) {
        String msg = "Неверная дата. Введите дату в корректном формате. Например: dd/MM/yyyy";

        log.error(msg);
        return new ResponseEntity<>(new ErrorResponse(msg, LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ForbiddenException e) {
        log.error("Ошибка из-за ограничений доступа: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotAllowedException e) {
        log.error("Ошибка: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.METHOD_NOT_ALLOWED);
    }
}
