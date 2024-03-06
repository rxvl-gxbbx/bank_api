package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import com.rxvlvxr.bank.dtos.Response;
import com.rxvlvxr.bank.dtos.TransferDTO;
import com.rxvlvxr.bank.exceptions.*;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.AccountService;
import com.rxvlvxr.bank.utils.ErrorUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/bank/accounts")
@Slf4j
public class AccountsController {
    private final AccountService accountService;

    @Autowired
    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PatchMapping("/{id}/transfer")
    public ResponseEntity<Response> transfer(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails,
                                             @RequestBody @Valid TransferDTO request, BindingResult bindingResult) {
        User user = userDetails.user();
        log.info("Метод transfer начал выполнение для пользователя={}", user.getUsername());

        long accountId = user.getAccount().getId();

        if (accountId != id) throw new ForbiddenException();
        if (accountId == request.getAccountId()) throw new NotAllowedException();
        if (bindingResult.hasErrors()) throw new TransferNotProcessedException(ErrorUtil.getResponse(bindingResult));

        log.info("Попытка перевода с аккаунта id={} на аккаунт id={}, сумма перевода={}", id, request.getAccountId(), request.getAmount());
        accountService.transfer(id, request.getAccountId(), request.getAmount());

        log.info("Перевод от пользователя={} на сумму={} выполнен успешно", user.getUsername(), request.getAmount());
        return new ResponseEntity<>(new Response("Перевод выполнен успешно", LocalDateTime.now()), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(TransferNotProcessedException e) {
        log.error("Ошибка валидации!");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AccountNotFoundException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotEnoughFundsException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.BAD_REQUEST);
    }
}
