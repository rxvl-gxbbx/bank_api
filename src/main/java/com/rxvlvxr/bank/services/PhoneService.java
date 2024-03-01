package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.PhoneRepository;
import com.rxvlvxr.bank.repositories.UserRepository;
import com.rxvlvxr.bank.utils.NotAllowedException;
import com.rxvlvxr.bank.utils.PhoneNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Optional<Phone> phoneOptional = phoneRepository.findById(id);

        phoneOptional.ifPresentOrElse(phone -> {
            phone.setNumber(updated.getNumber());

            phoneRepository.save(phone);
        }, () -> {
            throw new PhoneNotFoundException();
        });
    }

    @Transactional
    public void delete(long id) {
        Optional<Phone> optionalPhone = phoneRepository.findById(id);

        optionalPhone.ifPresentOrElse(phone -> {
            Optional<User> optionalUser = userRepository.findById(phone.getUser().getId());

            optionalUser.ifPresent(user -> {
                List<Phone> phones = user.getPhones();
                phones.remove(phone);

                if (phones.stream().allMatch(another -> another.getCreatedAt().isBefore(phone.getCreatedAt())))
                    throw new NotAllowedException();
            });

            phoneRepository.delete(phone);
        }, () -> {
            throw new PhoneNotFoundException();
        });
    }
}
