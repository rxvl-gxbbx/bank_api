package com.rxvlvxr.bank.validators;

import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidation implements Validator {
    private final UserRepository userRepository;

    @Autowired
    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        userRepository.findByUsername(user.getUsername()).ifPresent(val -> errors.rejectValue("username", "", "Пользователь с таким логином уже существует"));
    }
}
