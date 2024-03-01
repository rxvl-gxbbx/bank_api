package com.rxvlvxr.bank;

import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.repositories.AccountRepository;
import com.rxvlvxr.bank.services.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;

@SpringBootTest
class BankApplicationTests {

    final AccountRepository accountRepository;
    final AccountService accountService;

    @Autowired
    BankApplicationTests(AccountRepository accountRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @Test
    void transferCorrectAmount() {
        double amount = 123.123;

        List<Account> accounts = accountRepository.findAll().stream().sorted(Comparator.comparingLong(Account::getId)).toList();
        Assertions.assertTrue(accounts.size() >= 2);

        accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
        Assertions.assertEquals(accounts.get(0).getAmount() - amount, accountRepository.findById(accounts.get(0).getId()).get().getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount() + amount, accountRepository.findById(accounts.get(1).getId()).get().getAmount());
    }

    @Test
    void transferFromOneToOther26TimesGetCorrectAmount() {
        double amount = 123.123;

        List<Account> accounts = accountRepository.findAll().stream().sorted(Comparator.comparingLong(Account::getId)).toList();
        Assertions.assertTrue(accounts.size() >= 2);

        for (int i = 0; i < 26; i++) {
            accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
            accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
        }

        Assertions.assertEquals(accounts.get(0).getAmount(), accountRepository.findById(accounts.get(0).getId()).get().getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount(), accountRepository.findById(accounts.get(1).getId()).get().getAmount());
    }

    @Test
    void transferFromOneToOtherInDifferentThread25TimesGetCurrentAmount() throws InterruptedException {
        double amount = 123.123;

        List<Account> accounts = accountRepository.findAll().stream().sorted(Comparator.comparingLong(Account::getId)).toList();
        Assertions.assertTrue(accounts.size() >= 2);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 25; i++) {
                accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
                accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 25; i++) {
                accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
                accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        Assertions.assertEquals(accounts.get(0).getAmount(), accountRepository.findById(accounts.get(0).getId()).get().getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount(), accountRepository.findById(accounts.get(1).getId()).get().getAmount());
    }
}
