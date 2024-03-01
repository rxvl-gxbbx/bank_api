package com.rxvlvxr.bank.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {
    @NotNull(message = "поле не может быть пустым")
    @Min(value = 0)
    private Double amount;
}
