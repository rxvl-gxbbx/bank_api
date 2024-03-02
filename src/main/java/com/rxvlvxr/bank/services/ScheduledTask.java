package com.rxvlvxr.bank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration-test")
public class ScheduledTask {
    private final AccountService accountService;

    @Autowired
    public ScheduledTask(AccountService accountService) {
        this.accountService = accountService;
    }

    @Scheduled(fixedRate = 60_000)
    public void increaseBalance() {
        accountService.increaseBalanceForAllAccounts();
    }
}
