package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.PhoneDTO;
import com.rxvlvxr.bank.mappers.PhoneMapper;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.PhoneService;
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
@RequestMapping("/phones")
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
    public ResponseEntity<String> add(@AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid PhoneDTO phoneDTO, BindingResult bindingResult) {
        log.info("Метод add начал выполнение для пользователя: {}", userDetails.getUser().getUsername());

        Phone phone = phoneMapper.toPhone(phoneDTO);

        log.info("Валидация номера телефона");
        phoneValidation.validate(phone, bindingResult);

        if (bindingResult.hasErrors()) throw new PhoneNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        User user = userDetails.getUser();
        phone.setUser(user);

        log.info("Попытка добавить новый номер для пользователя: {}", user.getUsername());
        phoneService.add(phone);

        log.info("Телефон успешно добавлен для пользователя: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Телефон успешно добавлен");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid PhoneDTO phoneDTO, BindingResult bindingResult) {
        User user = userDetails.getUser();

        log.info("Метод update начал выполнение для пользователя: {}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        Phone phone = phoneMapper.toPhone(phoneDTO);

        log.info("Валидация номера телефона");
        phoneValidation.validate(phone, bindingResult);

        if (bindingResult.hasErrors())
            throw new PhoneNotUpdatedException(ErrorUtil.getErrorMsg(bindingResult));

        phone.setUser(user);

        log.info("Обновление телефона с идентификатором {} на номер {}", id, phone.getNumber());
        phoneService.update(id, phone);

        log.info("Телефон успешно обновлен для пользователя: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("Телефон успешно обновлен");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails) {
        User user = userDetails.getUser();

        log.info("Метод delete начал выполнение для пользователя: {}", user.getUsername());

        if (isRestricted(id, user)) throw new ForbiddenException();

        log.info("Удаляется телефон с идентификатором: {}", id);
        phoneService.delete(id);

        log.info("Телефон успешно удален для пользователя: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("Телефон успешно удален");
    }

    private boolean isRestricted(long id, User user) {
        return user.getPhones().stream().noneMatch(busyPhone -> busyPhone.getId() == id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PhoneNotCreatedException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PhoneNotUpdatedException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PhoneNotFoundException e) {
        log.error("Ошибка: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

}
