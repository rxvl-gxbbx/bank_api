package com.rxvlvxr.bank.utils;

import com.rxvlvxr.bank.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContactInfoWrapper {
    private LocalDateTime createdAt;
    private User user;
}
