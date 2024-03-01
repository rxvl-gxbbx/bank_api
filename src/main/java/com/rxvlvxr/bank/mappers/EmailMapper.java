package com.rxvlvxr.bank.mappers;

import com.rxvlvxr.bank.dtos.EmailDTO;
import com.rxvlvxr.bank.models.Email;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public EmailMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Email toEmail(EmailDTO emailDTO) {
        return modelMapper.map(emailDTO, Email.class);
    }
}
