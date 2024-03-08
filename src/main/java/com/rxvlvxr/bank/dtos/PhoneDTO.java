package com.rxvlvxr.bank.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PhoneDTO {
    @NotBlank(message = "поле не может быть пустым")
    @Pattern(regexp = "7\\d{10}", message = "неверный номер телефона. Введите номер в корректном формате. Например: 79001234567")
    private String number;

    @Override
    public String toString() {
        return number;
    }
}
