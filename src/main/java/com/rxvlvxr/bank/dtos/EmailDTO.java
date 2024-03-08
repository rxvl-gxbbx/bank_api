package com.rxvlvxr.bank.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EmailDTO {
    @NotBlank(message = "поле не может быть пустым")
    @jakarta.validation.constraints.Email(message = "пожалуйста, введите корректный адрес электронной почты")
    private String address;

    @Override
    public String toString() {
        return address;
    }
}
