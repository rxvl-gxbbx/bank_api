package com.rxvlvxr.bank.mappers;

import com.rxvlvxr.bank.dtos.PhoneDTO;
import com.rxvlvxr.bank.models.Phone;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhoneMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public PhoneMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Phone toPhone(PhoneDTO phoneDTO) {
        return modelMapper.map(phoneDTO, Phone.class);
    }
}
