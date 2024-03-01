package com.rxvlvxr.bank.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class SearchDTO {
    private LocalDate birthDate;
    private PhoneDTO phone;
    private String fullName;
    private EmailDTO email;
}
