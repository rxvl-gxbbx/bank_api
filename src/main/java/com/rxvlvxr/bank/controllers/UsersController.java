package com.rxvlvxr.bank.controllers;

import com.rxvlvxr.bank.dtos.*;
import com.rxvlvxr.bank.exceptions.InvalidLoginRequestException;
import com.rxvlvxr.bank.exceptions.UserNotCreatedException;
import com.rxvlvxr.bank.mappers.EmailMapper;
import com.rxvlvxr.bank.mappers.PhoneMapper;
import com.rxvlvxr.bank.mappers.UserMapper;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.security.BankUserDetails;
import com.rxvlvxr.bank.services.RegistrationService;
import com.rxvlvxr.bank.services.UserService;
import com.rxvlvxr.bank.utils.ErrorUtil;
import com.rxvlvxr.bank.utils.JWTUtil;
import com.rxvlvxr.bank.validators.EmailValidation;
import com.rxvlvxr.bank.validators.PhoneValidation;
import com.rxvlvxr.bank.validators.UserValidation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    private final PhoneMapper phoneMapper;
    private final EmailMapper emailMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UsersController(RegistrationService registrationService, UserService userService, UserValidation userValidation, PhoneValidation phoneValidation, EmailValidation emailValidation, UserMapper userMapper, PhoneMapper phoneMapper, EmailMapper emailMapper, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.userValidation = userValidation;
        this.phoneValidation = phoneValidation;
        this.emailValidation = emailValidation;
        this.userMapper = userMapper;
        this.phoneMapper = phoneMapper;
        this.emailMapper = emailMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/search")
    public UserResponse searchResults(@RequestBody SearchDTO request, @AuthenticationPrincipal BankUserDetails userDetails) {
        log.info("Метод searchResults начал выполнение для пользователя={}", userDetails.user().getUsername());

        PhoneDTO phoneDTO = request.getPhone();
        EmailDTO emailDTO = request.getEmail();

        Phone phone = phoneDTO != null ? phoneMapper.toPhone(phoneDTO) : null;
        Email email = emailDTO != null ? emailMapper.toEmail(emailDTO) : null;

        log.info("Выполняется поиск пользователей с одним из параметром: birthDate, phone.number, fullName, email.address");
        UserResponse response = new UserResponse(userService.search(request.getBirthDate(), phone, request.getFullName(), email).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList()));

        log.info("Поиск пользователей успешно завершен");
        log.info("Количество найденных пользователей: {}", response.getUsers().size());
        return response;
    }

    @PostMapping("/registration")
    public Map<String, String> registration(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        log.info("Метод registration начал выполнение");

        User user = userMapper.toUser(userDTO);

        log.info("Валидация логина пользователя");
        userValidation.validate(user, bindingResult);

        List<Phone> phones = user.getPhones();
        List<Email> emails = user.getEmails();

        if (phones != null && emails != null && !phones.isEmpty() && !emails.isEmpty()) {
            Phone phone = phones.get(0);
            Email email = emails.get(0);

            log.info("Валидация номера телефона");
            phoneValidation.validate(phone, bindingResult);
            log.info("Валидация адреса почты");
            emailValidation.validate(email, bindingResult);
        }

        if (bindingResult.hasErrors())
            throw new UserNotCreatedException(ErrorUtil.getResponse(bindingResult));

        log.info("Регистрация пользователя={}", user.getUsername());
        registrationService.register(user);
        log.info("Регистрация прошла успешно");

        log.info("Генерация токена");
        String token = jwtUtil.generateToken(userDTO.getUsername());

        log.info("Токен успешно сгенерирован");
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody @Valid AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                authenticationDTO.getPassword());
        if (bindingResult.hasErrors())
            throw new InvalidLoginRequestException(ErrorUtil.getResponse(bindingResult));

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(Map.of("message", "Incorrect credentials"), HttpStatus.FORBIDDEN);
        }

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
}
