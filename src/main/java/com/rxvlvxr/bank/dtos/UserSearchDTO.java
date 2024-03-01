package com.rxvlvxr.bank.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserSearchDTO {
    private String fullName;
    private LocalDate birthDate;
    private AccountDTO account;
    private List<PhoneDTO> phones;
    private List<EmailDTO> emails;
}
