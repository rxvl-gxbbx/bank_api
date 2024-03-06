package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.exceptions.EmailNotFoundException;
import com.rxvlvxr.bank.exceptions.NotAllowedException;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.repositories.EmailRepository;
import com.rxvlvxr.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class EmailService {
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmailService(EmailRepository emailRepository, UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void add(Email email) {
        email.setCreatedAt(LocalDateTime.now());

        emailRepository.save(email);
    }

    @Transactional
    public void update(long id, Email updated) {
        emailRepository.findById(id).ifPresentOrElse(email -> {
            email.setAddress(updated.getAddress());

            emailRepository.save(email);
        }, EmailNotFoundException::new);
    }

    @Transactional
    public void delete(long id) {
        emailRepository.findById(id).ifPresentOrElse(email -> {
            userRepository.findById(email.getUser().getId()).ifPresent(user -> {
                if (user.getEmails().stream()
                        .filter(val -> val != email)
                        .allMatch(another -> another.getCreatedAt().isBefore(email.getCreatedAt())))
                    throw new NotAllowedException();
            });

            emailRepository.delete(email);
        }, EmailNotFoundException::new);
    }
}
