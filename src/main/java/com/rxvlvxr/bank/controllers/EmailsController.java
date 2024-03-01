package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.EmailDTO;
import com.rxvlvxr.bank.mappers.EmailMapper;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.EmailService;
import com.rxvlvxr.bank.utils.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/emails")
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
    public ResponseEntity<String> add(@AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid EmailDTO emailDTO, BindingResult bindingResult) {
        log.info("Метод add начал выполнение для пользователя: {}", userDetails.getUser().getUsername());

        Email email = emailMapper.toEmail(emailDTO);

        log.info("Валидация почты");
        emailValidation.validate(email, bindingResult);

        if (bindingResult.hasErrors()) throw new EmailNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        User user = userDetails.getUser();
        email.setUser(user);

        log.info("Добавление почты для пользователя: {}", user.getUsername());
        emailService.add(email);

        log.info("Адрес почты успешно добавлен для пользователя: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Почта успешно добавлена");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid EmailDTO emailDTO, BindingResult bindingResult) {
        User user = userDetails.getUser();

        log.info("Метод update начал выполнение для пользователя: {}", user.getUsername());

        if (isRestricted(id, user))
            throw new ForbiddenException();

        Email email = emailMapper.toEmail(emailDTO);

        log.info("Валидация почты");
        emailValidation.validate(email, bindingResult);

        if (bindingResult.hasErrors())
            throw new EmailNotUpdatedException(ErrorUtil.getErrorMsg(bindingResult));

        email.setUser(user);

        log.info("Обновление почты с id {} на адрес {}", id, email.getAddress());
        emailService.update(id, email);

        log.info("Почта успешно обновлена для пользователя: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("Почта успешно обновлена");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails) {
        User user = userDetails.getUser();

        log.info("Метод delete начал выполнение для пользователя: {}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        log.info("Удаляется почта с идентификатором: {}", id);
        emailService.delete(id);

        log.info("Почта успешно удалена для пользователя: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("Почта успешно удалена");
    }

    private boolean isRestricted(long id, User user) {
        return user.getEmails().stream().noneMatch(busyMail -> busyMail.getId() == id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EmailNotCreatedException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EmailNotFoundException e) {
        log.error("Ошибка: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EmailNotUpdatedException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }
}
