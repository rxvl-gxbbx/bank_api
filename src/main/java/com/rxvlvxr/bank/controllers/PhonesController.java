package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.ErrorResponse;
import com.rxvlvxr.bank.dtos.PhoneDTO;
import com.rxvlvxr.bank.dtos.Response;
import com.rxvlvxr.bank.exceptions.ForbiddenException;
import com.rxvlvxr.bank.exceptions.PhoneNotCreatedException;
import com.rxvlvxr.bank.exceptions.PhoneNotFoundException;
import com.rxvlvxr.bank.exceptions.PhoneNotUpdatedException;
import com.rxvlvxr.bank.mappers.PhoneMapper;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.PhoneService;
import com.rxvlvxr.bank.utils.ErrorUtil;
import com.rxvlvxr.bank.validators.PhoneValidation;
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
@RequestMapping("/bank/phones")
@Slf4j
public class PhonesController {
    private final PhoneValidation phoneValidation;
    private final PhoneMapper phoneMapper;
    private final PhoneService phoneService;

    @Autowired
    public PhonesController(PhoneValidation phoneValidation, PhoneMapper phoneMapper, PhoneService phoneService) {
        this.phoneValidation = phoneValidation;
        this.phoneMapper = phoneMapper;
        this.phoneService = phoneService;
    }

    @PostMapping
    public ResponseEntity<Response> add(@AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid PhoneDTO phoneDTO, BindingResult bindingResult) {
        User user = userDetails.user();
        log.info("Метод add начал выполнение для пользователя={}", user.getUsername());

        Phone phone = phoneMapper.toPhone(phoneDTO);

        log.info("Валидация номера телефона");
        phoneValidation.validate(phone, bindingResult);

        if (bindingResult.hasErrors()) throw new PhoneNotCreatedException(ErrorUtil.getResponse(bindingResult));

        phone.setUser(user);

        log.info("Попытка добавить новый номер для пользователя={}", user.getUsername());
        phoneService.add(phone);

        log.info("Номер телефона успешно добавлен для пользователя={}", user.getUsername());
        return new ResponseEntity<>(new Response("Номер телефона успешно добавлен", LocalDateTime.now()), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> update(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid PhoneDTO phoneDTO, BindingResult bindingResult) {
        User user = userDetails.user();
        log.info("Метод update начал выполнение для пользователя={}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        Phone phone = phoneMapper.toPhone(phoneDTO);

        log.info("Валидация номера телефона");
        phoneValidation.validate(phone, bindingResult);

        if (bindingResult.hasErrors())
            throw new PhoneNotUpdatedException(ErrorUtil.getResponse(bindingResult));

        phone.setUser(user);

        user.getPhones().stream()
                .findFirst()
                .ifPresentOrElse(telephone -> log.info("Обновление телефона id={} с номера=\"{}\" на номер=\"{}\"", id, telephone.getNumber(), phone.getNumber()),
                        PhoneNotFoundException::new);
        phoneService.update(id, phone);

        log.info("Номер телефона успешно обновлен для пользователя={}", user.getUsername());
        return new ResponseEntity<>(new Response("Номер телефона успешно обновлен", LocalDateTime.now()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails) {
        User user = userDetails.user();
        log.info("Метод delete начал выполнение для пользователя={}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        user.getPhones().stream()
                .findFirst()
                .ifPresentOrElse(phone -> log.info("Удаляется телефон id={} с номером {} для пользователя {}", id, phone.getNumber(), user.getUsername()),
                        PhoneNotFoundException::new);
        phoneService.delete(id);

        log.info("Номер телефона успешно удален для пользователя={}", user.getUsername());
        return new ResponseEntity<>(new Response("Номер телефона успешно удален", LocalDateTime.now()), HttpStatus.OK);
    }

    private boolean isRestricted(long id, User user) {
        return user.getPhones().stream()
                .noneMatch(busyPhone -> busyPhone.getId() == id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PhoneNotCreatedException e) {
        log.error("Ошибка валидации!");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PhoneNotUpdatedException e) {
        log.error("Ошибка валидации!");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PhoneNotFoundException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.BAD_REQUEST);
    }
}
