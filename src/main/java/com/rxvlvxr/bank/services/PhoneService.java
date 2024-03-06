package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.exceptions.NotAllowedException;
import com.rxvlvxr.bank.exceptions.PhoneNotFoundException;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.repositories.PhoneRepository;
import com.rxvlvxr.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, UserRepository userRepository) {
        this.phoneRepository = phoneRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void add(Phone phone) {
        phone.setCreatedAt(LocalDateTime.now());

        phoneRepository.save(phone);
    }

    @Transactional
    public void update(long id, Phone updated) {
        phoneRepository.findById(id).ifPresentOrElse(phone -> {
            phone.setNumber(updated.getNumber());

            phoneRepository.save(phone);
        }, PhoneNotFoundException::new);
    }

    @Transactional
    public void delete(long id) {
        phoneRepository.findById(id).ifPresentOrElse(phone -> {
            userRepository.findById(phone.getUser().getId()).ifPresent(user -> {
                if (user.getPhones().stream()
                        .filter(val -> val != phone)
                        .allMatch(another -> another.getCreatedAt().isBefore(phone.getCreatedAt())))
                    throw new NotAllowedException();
            });

            phoneRepository.delete(phone);
        }, PhoneNotFoundException::new);
    }
}
