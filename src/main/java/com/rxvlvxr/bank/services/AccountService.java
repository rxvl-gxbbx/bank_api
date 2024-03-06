package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.exceptions.AccountNotFoundException;
import com.rxvlvxr.bank.exceptions.NotEnoughFundsException;
import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        accountRepository.findAll().forEach(account -> {
            double newBalance = account.getAmount() * 1.05;
            double maxBalance = account.getInitDeposit() * 2.07;

            if (newBalance <= maxBalance)
                account.setAmount(newBalance);
        });

        accountRepository.flush();
    }

    @Transactional
    public synchronized void transfer(long fromId, long toId, double amount) {
        Account accountFrom = accountRepository.findById(fromId).orElseThrow(AccountNotFoundException::new);
        Account accountTo = accountRepository.findById(toId).orElseThrow(AccountNotFoundException::new);

        if (amount > accountFrom.getAmount())
            throw new NotEnoughFundsException(accountFrom.getAmount());

        accountFrom.setAmount(accountFrom.getAmount() - amount);
        accountTo.setAmount(accountTo.getAmount() + amount);

        accountRepository.flush();
    }
}
