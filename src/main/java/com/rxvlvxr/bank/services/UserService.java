package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.daos.UserDAO;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserDAO userDAO;
    private final AccountService accountService;

    @Autowired
    public UserService(UserRepository userRepository, UserDAO userDAO, AccountService accountService) {
        this.userRepository = userRepository;
        this.userDAO = userDAO;
        this.accountService = accountService;
    }

    @Transactional
    public List<User> search(LocalDate date, Phone phone, String name, Email email) {
        accountService.increaseBalanceForAllAccounts();

        List<User> users = new ArrayList<>();

        if (date != null)
            users = userRepository.findAllByBirthDateAfter(date);
        else if (phone != null)
            users = userDAO.findAllByPhoneNumber(phone.getNumber());
        else if (name != null)
            users = userRepository.findAllByFullNameStartingWith(name);
        else if (email != null)
            users = userDAO.findAllByEmail(email.getAddress());

        return users;
    }
}
