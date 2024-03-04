package com.rxvlvxr.bank.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AuthenticationDTO {
    @NotNull(message = "поле не может быть пустым")
    private String username;
    @NotNull(message = "поле не может быть пустым")
    private String password;
}
