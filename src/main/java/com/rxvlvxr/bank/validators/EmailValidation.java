package com.rxvlvxr.bank.validators;

import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.repositories.EmailRepository;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EmailValidation implements Validator {
    private final EmailRepository emailRepository;

    @Autowired
    public EmailValidation(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Email.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Email email = (Email) target;
        String busy = "Этот адрес электронной почты уже используется";

        emailRepository.findByAddress(email.getAddress()).ifPresent(val -> {
            try {
                errors.rejectValue("address", "", busy);
            } catch (NotReadablePropertyException e) {
                errors.rejectValue("email.address", "", busy);
            }
        });
    }
}
