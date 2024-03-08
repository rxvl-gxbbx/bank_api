package com.rxvlvxr.bank.mappers;

import com.rxvlvxr.bank.dtos.SearchDTO;
import com.rxvlvxr.bank.dtos.UserDTO;
import com.rxvlvxr.bank.dtos.UserSearchDTO;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        user.setPhones(Collections.singletonList(new Phone(userDTO.getPhone().getNumber())));
        user.setEmails(Collections.singletonList(new Email(userDTO.getEmail().getAddress())));

        return user;
    }

    public User toUser(SearchDTO searchDTO) {
        User user = modelMapper.map(searchDTO, User.class);

        if (searchDTO.getPhone() != null)
            user.setPhones(Collections.singletonList(new Phone(searchDTO.getPhone().getNumber())));
        if (searchDTO.getEmail() != null)
            user.setEmails(Collections.singletonList(new Email(searchDTO.getEmail().getAddress())));

        return user;
    }

    public UserSearchDTO toDTO(User user) {
        return modelMapper.map(user, UserSearchDTO.class);
    }
}
