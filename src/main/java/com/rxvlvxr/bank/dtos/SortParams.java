package com.rxvlvxr.bank.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SortParams {
    @Pattern(regexp = "id|fullName|birthDate|account.createdAt|phones.number|emails.address", message = "должно соответствовать одному из перечисленных значений: fullName, birthDate, account.createdAt, phones.number, phones.createdAt, emails.address, emails.createdAt")
    private String field;
    @Pattern(regexp = "asc|desc", message = "пожалуйста, введите корректное значение: asc (по возрастанию), desc (по убыванию)")
    private String direction;
}