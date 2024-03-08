package com.rxvlvxr.bank.dtos;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PaginationParams {
    @Min(value = 1)
    private Integer pageNumber;
    @Min(value = 1)
    private Integer pageSize;
}