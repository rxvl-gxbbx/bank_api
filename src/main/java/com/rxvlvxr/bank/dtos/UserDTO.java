package com.rxvlvxr.bank.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "поле не может быть пустым")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+", message = "неверный формат. Введите ФИО. Например: Фамилия Имя Отчество")
    private String fullName;
    @NotNull(message = "поле не может быть пустым")
    private LocalDate birthDate;
    @NotBlank(message = "поле не может быть пустым")
    private String username;
    @NotBlank(message = "поле не может быть пустым")
    private String password;
    @Valid
    @NotNull(message = "поле не может быть пустым")
    private AccountDTO account;
    @Valid
    @NotNull(message = "поле не может быть пустым")
    private PhoneDTO phone;
    @Valid
    @NotNull(message = "поле не может быть пустым")
    private EmailDTO email;
}
