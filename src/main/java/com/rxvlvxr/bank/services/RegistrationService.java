package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.UserRepository;
import com.rxvlvxr.bank.utils.ContactInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

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

        setCreatedAtAndUser(user.getPhones().stream().findFirst(), user);
        setCreatedAtAndUser(user.getEmails().stream().findFirst(), user);

        Account account = user.getAccount();

        account.setCreatedAt(LocalDateTime.now());
        account.setUser(user);
        account.setInitDeposit(user.getAccount().getAmount());

        userRepository.save(user);
    }

    @Transactional
    protected void setCreatedAtAndUser(Optional<? extends ContactInfoWrapper> contactInfo, User user) {
        contactInfo.ifPresent(contact -> {
            contact.setCreatedAt(LocalDateTime.now());
            contact.setUser(user);
        });
    }
}
