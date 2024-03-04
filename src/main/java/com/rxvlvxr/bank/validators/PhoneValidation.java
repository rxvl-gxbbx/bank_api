package com.rxvlvxr.bank.validators;

import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.repositories.PhoneRepository;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PhoneValidation implements Validator {
    private final PhoneRepository phoneRepository;

    @Autowired
    public PhoneValidation(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Phone.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Phone phone = (Phone) target;
        String busy = "Этот номер уже используется";

        phoneRepository.findByNumber(phone.getNumber()).ifPresent(val -> {
            try {
                errors.rejectValue("number", "", busy);
            } catch (NotReadablePropertyException e) {
                errors.rejectValue("phone.number", "", busy);
            }
        });
    }
}
