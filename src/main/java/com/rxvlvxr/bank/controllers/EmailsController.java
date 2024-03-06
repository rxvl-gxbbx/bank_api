package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.EmailDTO;
import com.rxvlvxr.bank.dtos.ErrorResponse;
import com.rxvlvxr.bank.dtos.Response;
import com.rxvlvxr.bank.exceptions.EmailNotCreatedException;
import com.rxvlvxr.bank.exceptions.EmailNotFoundException;
import com.rxvlvxr.bank.exceptions.EmailNotUpdatedException;
import com.rxvlvxr.bank.exceptions.ForbiddenException;
import com.rxvlvxr.bank.mappers.EmailMapper;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.EmailService;
import com.rxvlvxr.bank.utils.ErrorUtil;
import com.rxvlvxr.bank.validators.EmailValidation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/bank/emails")
@Slf4j
public class EmailsController {
    private final EmailMapper emailMapper;
    private final EmailValidation emailValidation;
    private final EmailService emailService;

    public EmailsController(EmailMapper emailMapper, EmailValidation emailValidation, EmailService emailService) {
        this.emailMapper = emailMapper;
        this.emailValidation = emailValidation;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Response> add(@AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid EmailDTO emailDTO, BindingResult bindingResult) {
        User user = userDetails.user();
        log.info("Метод add начал выполнение для пользователя: {}", user.getUsername());

        Email email = emailMapper.toEmail(emailDTO);

        log.info("Валидация почты");
        emailValidation.validate(email, bindingResult);

        if (bindingResult.hasErrors()) throw new EmailNotCreatedException(ErrorUtil.getResponse(bindingResult));

        email.setUser(user);

        log.info("Добавление адреса почты для пользователя: {}", user.getUsername());
        emailService.add(email);

        log.info("Адрес почты успешно добавлен для пользователя: {}", user.getUsername());
        return new ResponseEntity<>(new Response("Почта успешно добавлена", LocalDateTime.now()), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> update(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid EmailDTO emailDTO, BindingResult bindingResult) {
        User user = userDetails.user();
        log.info("Метод update начал выполнение для пользователя: {}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        Email email = emailMapper.toEmail(emailDTO);

        log.info("Валидация почты");
        emailValidation.validate(email, bindingResult);

        if (bindingResult.hasErrors()) throw new EmailNotUpdatedException(ErrorUtil.getResponse(bindingResult));

        email.setUser(user);

        user.getEmails().stream()
                .findFirst()
                .ifPresentOrElse(mail -> log.info("Обновление почты id={} c адреса \"{}\" на адрес \"{}\"", id, mail.getAddress(), email.getAddress()),
                        EmailNotFoundException::new);
        emailService.update(id, email);

        log.info("Почта успешно обновлена для пользователя: {}", user.getUsername());
        return new ResponseEntity<>(new Response("Почта успешно обновлена", LocalDateTime.now()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails) {
        User user = userDetails.user();
        log.info("Метод delete начал выполнение для пользователя: {}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        user.getEmails().stream()
                .findFirst()
                .ifPresentOrElse(email -> log.info("Удаляется почта id={} с адресом \"{}\" для пользователя {}", id, email.getAddress(), user.getUsername()),
                        EmailNotFoundException::new);
        emailService.delete(id);

        log.info("Почта успешно удалена для пользователя: {}", user.getUsername());
        return new ResponseEntity<>(new Response("Почта успешно удалена", LocalDateTime.now()), HttpStatus.OK);
    }

    private boolean isRestricted(long id, User user) {
        return user.getEmails().stream().noneMatch(busyMail -> busyMail.getId() == id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EmailNotCreatedException e) {
        log.error("Ошибка валидации!");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EmailNotUpdatedException e) {
        log.error("Ошибка валидации!");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EmailNotFoundException e) {
        log.error("Ошибка: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.BAD_REQUEST);
    }
}
