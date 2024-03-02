package com.rxvlvxr.bank.services;

import com.rxvlvxr.bank.daos.AccountDAO;
import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.repositories.AccountRepository;
import com.rxvlvxr.bank.utils.NotEnoughFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountDAO accountDAO;
    @Autowired
    public AccountService(AccountRepository accountRepository, AccountDAO accountDAO) {
        this.accountRepository = accountRepository;
        this.accountDAO = accountDAO;
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
        List<Account> accounts = accountDAO.selectForUpdate(fromId, toId);

        Account accountFrom = accounts.get(0);
        Account accountTo = accounts.get(1);

        if (amount > accountFrom.getAmount())
            throw new NotEnoughFundsException();

        accountFrom.setAmount(accountFrom.getAmount() - amount);
        accountTo.setAmount(accountTo.getAmount() + amount);

        accountRepository.saveAll(List.of(accountFrom, accountTo));
    }
}
