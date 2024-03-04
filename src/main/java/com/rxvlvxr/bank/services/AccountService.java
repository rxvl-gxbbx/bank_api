package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.exceptions.AccountNotFoundException;
import com.rxvlvxr.bank.exceptions.NotEnoughFundsException;
import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AccountService {
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
    public synchronized void transfer(long fromId, long toId, double amount) {
        Account accountFrom = accountRepository.findById(fromId).orElse(null);
        Account accountTo = accountRepository.findById(toId).orElse(null);

        if (accountTo == null || accountFrom == null)
            throw new AccountNotFoundException();

        if (amount > accountFrom.getAmount())
            throw new NotEnoughFundsException();

        accountFrom.setAmount(accountFrom.getAmount() - amount);
        accountTo.setAmount(accountTo.getAmount() + amount);

        accountRepository.saveAll(List.of(accountFrom, accountTo));
    }
}
