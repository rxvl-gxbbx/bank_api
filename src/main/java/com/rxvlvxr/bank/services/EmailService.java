package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.EmailRepository;
import com.rxvlvxr.bank.repositories.UserRepository;
import com.rxvlvxr.bank.utils.EmailNotFoundException;
import com.rxvlvxr.bank.utils.NotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Optional<Email> emailOptional = emailRepository.findById(id);

        emailOptional.ifPresentOrElse(email -> {
            email.setAddress(updated.getAddress());

            emailRepository.save(email);
        }, () -> {
            throw new EmailNotFoundException();
        });
    }

    @Transactional
    public void delete(long id) {
        Optional<Email> optionalEmail = emailRepository.findById(id);

        optionalEmail.ifPresentOrElse(email -> {
            Optional<User> optionalUser = userRepository.findById(email.getUser().getId());

            optionalUser.ifPresent(user -> {
                List<Email> emails = user.getEmails();
                emails.remove(email);

                if (emails.stream().allMatch(another -> another.getCreatedAt().isBefore(email.getCreatedAt())))
                    throw new NotAllowedException();
            });

            emailRepository.delete(email);
        }, () -> {
            throw new EmailNotFoundException();
        });
    }
}
