package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.TransferDTO;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.AccountService;
import com.rxvlvxr.bank.utils.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountsController {
    private final AccountService accountService;

    @Autowired
    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PatchMapping("/{id}/transfer")
    public ResponseEntity<String> transfer(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails,
                                           @RequestBody @Valid TransferDTO request, BindingResult bindingResult) {
        User user = userDetails.getUser();

        log.info("Метод transfer начал выполнение для пользователя: {}", user.getUsername());

        if (user.getAccount().getId() != id)
            throw new ForbiddenException();

        if (bindingResult.hasErrors())
            throw new TransferNotProcessedException(ErrorUtil.getErrorMsg(bindingResult));

        log.info("Попытка перевода с аккаунта {} на аккаунт {}: {}", id, request.getAccountId(), request.getAmount());
        accountService.transfer(id, request.getAccountId(), request.getAmount());

        log.info("Перевод на сумму {} выполнен успешно", request.getAmount());
        return ResponseEntity.status(HttpStatus.OK).body("Перевод выполнен успешно");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(TransferNotProcessedException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AccountNotFoundException e) {
        log.error("Ошибка: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotEnoughFundsException e) {
        log.error("Ошибка: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }
}
