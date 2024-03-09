package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<User> search(Specification<User> specification, Pageable pageable) {
        return userRepository.findAll(specification, pageable).getContent();
    }
}
