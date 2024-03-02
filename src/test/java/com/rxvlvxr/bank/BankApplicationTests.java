package com.rxvlvxr.bank;

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
import java.util.*;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class BankApplicationTests {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final double amount = 123.123;
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
        User user1 = new User("First Test User", LocalDate.now().minusYears(18), "firstUsername", "firstPassword");
        User user2 = new User("Second Test User", LocalDate.now().minusYears(18), "secondUsername", "secondPassword");

        Account account1 = new Account(100_000, LocalDateTime.now(), user1, 100_000);
        Account account2 = new Account(100_000, LocalDateTime.now(), user2, 100_000);

        Phone phone1 = new Phone("8999123123456", LocalDateTime.now(), user1);
        Phone phone2 = new Phone("8777321654321", LocalDateTime.now(), user2);

        Email email1 = new Email("Test Address №1", LocalDateTime.now(), user1);
        Email email2 = new Email("Test Address №2", LocalDateTime.now(), user2);

        user1.setPhones(Collections.singletonList(phone1));
        user1.setEmails(Collections.singletonList(email1));

        user2.setPhones(Collections.singletonList(phone2));
        user2.setEmails(Collections.singletonList(email2));

        user1.setAccount(account1);
        user2.setAccount(account2);

        users = userRepository.saveAll(List.of(user1, user2));

        accounts = users.stream().map(User::getAccount).sorted(Comparator.comparingLong(Account::getId)).toList();
    }

    @AfterAll
    public void clear() {
        userRepository.deleteAll(users);
    }

    @Test
    void transferCorrectAmount() {
        Assertions.assertTrue(accounts.size() >= 2);

        accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);

        List<Account> accountsFromTable = getAccounts(accounts.get(0).getId(), accounts.get(1).getId());

        Assertions.assertEquals(accounts.get(0).getAmount() - amount, accountsFromTable.get(0).getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount() + amount, accountsFromTable.get(1).getAmount());
    }

    @Test
    void transferFromOneToOther26TimesGetCorrectAmount() {
        Assertions.assertTrue(accounts.size() >= 2);

        for (int i = 0; i < 26; i++) {
            accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
            accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
        }

        List<Account> accountsFromTable = getAccounts(accounts.get(0).getId(), accounts.get(1).getId());

        Assertions.assertEquals(accounts.get(0).getAmount(), accountsFromTable.get(0).getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount(), accountsFromTable.get(1).getAmount());
    }

    @Test
    void transferFromOneToOtherAccountIn10ParallelThreadsFor50TimesEachAndGetCurrentAmount() throws InterruptedException {
        double amount = 123.13;

        Assertions.assertTrue(accounts.size() >= 2);

        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    accountService.transfer(accounts.get(0).getId(), accounts.get(1).getId(), amount);
                    accountService.transfer(accounts.get(1).getId(), accounts.get(0).getId(), amount);
                }

                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        List<Account> accountsFromTable = getAccounts(accounts.get(0).getId(), accounts.get(1).getId());

        Assertions.assertEquals(accounts.get(0).getAmount(), accountsFromTable.get(0).getAmount());
        Assertions.assertEquals(accounts.get(1).getAmount(), accountsFromTable.get(1).getAmount());
    }

    private List<Account> getAccounts(long fromId, long toId) {
        List<Account> accs = new LinkedList<>();

        Optional<Account> optional1 = accountRepository.findById(fromId);
        Optional<Account> optional2 = accountRepository.findById(toId);

        if (optional1.isPresent() && optional2.isPresent()) {
            accs = List.of(optional1.get(), optional2.get());
        }

        return accs;
    }
}
