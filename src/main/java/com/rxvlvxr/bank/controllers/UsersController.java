package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.*;
import com.rxvlvxr.bank.exceptions.InvalidLoginRequestException;
import com.rxvlvxr.bank.exceptions.InvalidSearchRequestException;
import com.rxvlvxr.bank.exceptions.UserNotCreatedException;
import com.rxvlvxr.bank.mappers.UserMapper;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.RegistrationService;
import com.rxvlvxr.bank.services.UserService;
import com.rxvlvxr.bank.utils.ErrorUtil;
import com.rxvlvxr.bank.utils.JWTUtil;
import com.rxvlvxr.bank.utils.UserSpecification;
import com.rxvlvxr.bank.validators.EmailValidation;
import com.rxvlvxr.bank.validators.PhoneValidation;
import com.rxvlvxr.bank.validators.UserValidation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bank/users")
@Slf4j
public class UsersController {
    private final RegistrationService registrationService;
    private final UserService userService;
    private final UserValidation userValidation;
    private final PhoneValidation phoneValidation;
    private final EmailValidation emailValidation;
    private final UserMapper userMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UsersController(RegistrationService registrationService, UserService userService, UserValidation userValidation, PhoneValidation phoneValidation, EmailValidation emailValidation, UserMapper userMapper, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.userValidation = userValidation;
        this.phoneValidation = phoneValidation;
        this.emailValidation = emailValidation;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/search")
    public UserResponse searchResults(@RequestBody @Valid SearchDTO request, BindingResult bindingResult, @AuthenticationPrincipal BankUserDetails userDetails) {
        if (bindingResult.hasErrors())
            throw new InvalidSearchRequestException(ErrorUtil.getResponse(bindingResult));

        log.info("Метод searchResults начал выполнение для пользователя={}", userDetails.user().getUsername());
        User user = userMapper.toUser(request);
        log.info("Выполняется поиск пользователей с параметрами: birthDate={}, phones.number={}, fullName={}, emails.address={}", request.getBirthDate(), request.getPhone(), request.getFullName(), request.getEmail());
        UserResponse response = new UserResponse(userService.search(new UserSpecification(user), getPageable(request.getSort(), request.getPagination(), "fullName", 1, 10, Sort.Direction.ASC)).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList()));

        log.info("Поиск пользователей успешно завершен");
        log.info("Количество найденных пользователей: {}", response.getUsers().size());
        return response;
    }

    private Pageable getPageable(SortParams sortParams, PaginationParams paginationParams, String defaultField, int defaultPageNumber, int defaultPageSize, Sort.Direction defaultSortDirection) {
        String sortField = sortParams != null && sortParams.getField() != null ? sortParams.getField() : defaultField;
        Sort.Direction sortDirection = sortParams != null && sortParams.getDirection() != null ? Sort.Direction.fromString(sortParams.getDirection()) : defaultSortDirection;

        int pageNumber = paginationParams != null && paginationParams.getPageNumber() != null ? paginationParams.getPageNumber() : defaultPageNumber;
        int pageSize = paginationParams != null && paginationParams.getPageSize() != null ? paginationParams.getPageSize() : defaultPageSize;

        return PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortDirection, sortField));
    }

    @PostMapping("/registration")
    public Map<String, String> registration(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        log.info("Метод registration начал выполнение");

        User user = userMapper.toUser(userDTO);

        log.info("Валидация логина пользователя");
        userValidation.validate(user, bindingResult);

        validateContactInfo(user.getPhones().stream()
                .findFirst(), "телефона", phoneValidation, bindingResult);
        validateContactInfo(user.getEmails().stream()
                .findFirst(), "адреса почты", emailValidation, bindingResult);

        if (bindingResult.hasErrors()) throw new UserNotCreatedException(ErrorUtil.getResponse(bindingResult));

        log.info("Регистрация пользователя={}", user.getUsername());
        registrationService.register(user);
        log.info("Регистрация прошла успешно");

        log.info("Генерация токена");
        String token = jwtUtil.generateToken(userDTO.getUsername());

        log.info("Токен успешно сгенерирован");
        return Map.of("jwt-token", token);
    }

    private <T> void validateContactInfo(Optional<T> contactInfo, String contactType, Validator validator, BindingResult bindingResult) {
        contactInfo.ifPresentOrElse(info -> {
            log.info("Валидация " + contactType);
            validator.validate(info, bindingResult);
        }, () -> {
            throw new UserNotCreatedException(ErrorUtil.getResponse(bindingResult));
        });
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody @Valid AuthenticationDTO authenticationDTO, BindingResult bindingResult) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                authenticationDTO.getPassword());

        if (bindingResult.hasErrors()) throw new InvalidLoginRequestException(ErrorUtil.getResponse(bindingResult));

        authenticationManager.authenticate(authenticationToken);

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());

        return new ResponseEntity<>(Map.of("jwt-token", token), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(UserNotCreatedException e) {
        log.error("Ошибка валидации!");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(InvalidLoginRequestException e) {
        log.error("Ошибка валидации");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(InvalidSearchRequestException e) {
        log.error("Ошибка валидации");
        return new ResponseEntity<>(e.getResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(BadCredentialsException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(Collections.singletonList(new Response(e.getMessage(), LocalDateTime.now()))), HttpStatus.BAD_REQUEST);
    }
}
