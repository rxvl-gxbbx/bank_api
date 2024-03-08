package com.rxvlvxr.bank.dtos;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class SearchDTO {
    private LocalDate birthDate;
    @Valid
    private PhoneDTO phone;
    private String fullName;
    @Valid
    private EmailDTO email;
    @Valid
    private PaginationParams pagination;
    @Valid
    private SortParams sort;
}
