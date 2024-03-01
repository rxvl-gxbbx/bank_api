package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.getPhones().stream().findFirst().ifPresent(phone -> {
            phone.setCreatedAt(LocalDateTime.now());
            phone.setUser(user);
        });

        user.getEmails().stream().findFirst().ifPresent(email -> {
            email.setCreatedAt(LocalDateTime.now());
            email.setUser(user);
        });

        user.getAccount().setCreatedAt(LocalDateTime.now());
        user.getAccount().setUser(user);
        user.getAccount().setInitDeposit(user.getAccount().getAmount());

        userRepository.save(user);
    }
}
