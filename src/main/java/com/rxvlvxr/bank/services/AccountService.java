package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.repositories.AccountRepository;
import com.rxvlvxr.bank.utils.NotEnoughFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountService {
    private final static Object MONITOR = new Object();
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void increaseBalanceForAllAccounts() {
        List<Account> accounts = accountRepository.findAll();

        List<Account> accountList = accounts.stream().peek(account -> {
            double newBalance = account.getAmount() * 1.05;
            double maxBalance = account.getInitDeposit() * 2.07;

            if (newBalance <= maxBalance)
                account.setAmount(newBalance);

        }).toList();

        accountRepository.saveAll(accountList);
    }

    @Transactional
    public void transfer(long fromId, long toId, double amount) {
        MONITOR.notify();

        Optional<Account> optionalFrom = accountRepository.findById(fromId);
        Optional<Account> optionalTo = accountRepository.findById(toId);

        if (optionalFrom.isPresent() && optionalTo.isPresent()) {
            Account accountFrom = optionalFrom.get();
            Account accountTo = optionalTo.get();

            if (amount > accountFrom.getAmount())
                throw new NotEnoughFundsException();

            accountFrom.setAmount(accountFrom.getAmount() - amount);
            accountTo.setAmount(accountTo.getAmount() + amount);

            accountRepository.saveAll(List.of(accountFrom, accountTo));
        }
    }
}
