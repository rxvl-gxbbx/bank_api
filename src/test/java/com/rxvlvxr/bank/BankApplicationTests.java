package com.rxvlvxr.bank;

import com.rxvlvxr.bank.exceptions.AccountNotFoundException;
import com.rxvlvxr.bank.models.Account;
import com.rxvlvxr.bank.models.Email;
import com.rxvlvxr.bank.models.Phone;
import com.rxvlvxr.bank.models.User;
import com.rxvlvxr.bank.repositories.AccountRepository;
import com.rxvlvxr.bank.repositories.UserRepository;
import com.rxvlvxr.bank.services.AccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
@Transactional(readOnly = true)
class BankApplicationTests {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final double amount = 123.123;
    private final int capacity = 5;
    private List<Account> accounts;
    private List<User> users;

    @Autowired
    BankApplicationTests(AccountRepository accountRepository, UserRepository userRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    @BeforeAll
    @Transactional
    public void setUp() {
        users = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            User user = new User("Full Name Test" + i, LocalDate.now().minusYears(18 + i), "username" + i, "password" + i);

            Account account = new Account(100_000_000, LocalDateTime.now(), user, 100_000_000);
            Phone phone = new Phone("8900123121" + i, LocalDateTime.now(), user);
            Email email = new Email("mail" + i + "@mail.ru", LocalDateTime.now(), user);

            user.setAccount(account);
            user.setPhones(Collections.singletonList(phone));
            user.setEmails(Collections.singletonList(email));

            users.add(user);
        }

        userRepository.saveAll(users);

        accounts = users.stream()
                .map(User::getAccount)
                .sorted(Comparator.comparingLong(Account::getId))
                .toList();
    }

    @AfterAll
    @Transactional
    public void clear() {
        userRepository.deleteAll(users);
    }

    @Test
    @Transactional
    void isSizeCorrect() {
        Assertions.assertEquals(capacity, accounts.size());
    }

    @Test
    @Transactional
    void transferCorrectAmount() {
        accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);

        List<Account> accountsFromTable = getAccounts();

        Assertions.assertEquals(accounts.get(0).getAmount() - amount, accountsFromTable.get(0).getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount() + amount, accountsFromTable.get(1).getAmount());
    }

    @Test
    @Transactional
    void transferFromOneToOther26TimesGetCorrectAmount() {
        final int transferCount = 26;

        for (int i = 0; i < transferCount; i++) {
            accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
            accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
        }

        List<Account> accountsFromTable = getAccounts();

        Assertions.assertEquals(accounts.get(0).getAmount(), accountsFromTable.get(0).getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount(), accountsFromTable.get(1).getAmount());
    }

    @Test
    @Transactional
    void transferFromOneToOtherAccountIn10ParallelThreadsFor50TimesEachAndGetCurrentAmount() throws InterruptedException {
        final int transferCount = 50;
        final int threadCount = 10;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < transferCount; j++) {
                    accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
                    accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
                }

                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        List<Account> accountsFromTable = getAccounts();

        Assertions.assertEquals(accounts.get(0).getAmount(), accountsFromTable.get(0).getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount(), accountsFromTable.get(1).getAmount());
    }

    @Test
    @Transactional
    void transferToAccountFromThreeDifferentAccounts25TimesEachAndGetCurrentAmount() throws InterruptedException {
        final int maxIndex = accounts.size() - 1;
        final int count = 25;
        CountDownLatch countDownLatch = new CountDownLatch(maxIndex);

        for (int i = 0; i < maxIndex; i++) {
            int from = i;
            new Thread(() -> {
                for (int j = 0; j < count; j++) {
                    accountService.transfer(accounts.get(from).getId(), accounts.get(maxIndex).getId(), amount);
                }

                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        Account account = accountRepository.findById(accounts.get(maxIndex).getId()).orElseThrow(AccountNotFoundException::new);

        Assertions.assertEquals(accounts.get(maxIndex).getAmount() + count * amount * maxIndex, account.getAmount(), 0.001);
    }

    @Test
    @Transactional
    void transfer50TimesInCycleFrom10ParallelThreadsAndGetCorrectAmountInEachAccount() throws InterruptedException {
        final int transferCount = 50;
        final int threadCount = 10;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int x = 0; x < threadCount; x++) {
            new Thread(() -> {
                for (int y = 0; y < transferCount; y++) {
                    for (int i = 0; i < capacity; i++) {
                        for (int j = capacity - 1; j >= 0; j--) {
                            accountService.transfer(accounts.get(i).getId(), accounts.get(j).getId(), amount);
                        }
                    }
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        List<Account> accountsFromTable = getAccounts();

        for (int i = 0; i < capacity; i++) {
            Assertions.assertEquals(accounts.get(i).getAmount(), accountsFromTable.get(i).getAmount());
        }
    }

    private List<Account> getAccounts() {
        List<Account> accountsFromTable = accountRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Account::getId))
                .toList();

        accountsFromTable = accountsFromTable.stream()
                .skip(accountsFromTable.size() - accounts.size())
                .collect(Collectors.toList());

        return accountsFromTable;
    }
}
