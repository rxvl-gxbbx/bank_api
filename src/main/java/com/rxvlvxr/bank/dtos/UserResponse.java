package com.rxvlvxr.bank.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private List<UserSearchDTO> users;

    public UserResponse(List<UserSearchDTO> users) {
        this.users = users;
    }
}
