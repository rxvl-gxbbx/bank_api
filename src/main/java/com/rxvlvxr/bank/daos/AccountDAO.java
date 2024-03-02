package com.rxvlvxr.bank.daos;

import com.rxvlvxr.bank.models.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class AccountDAO {
    private final EntityManager entityManager;

    @Autowired
    public AccountDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Account> selectForUpdate(long fromId, long toId) {
        Account a = entityManager.find(Account.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        Account b = entityManager.find(Account.class, toId, LockModeType.PESSIMISTIC_WRITE);

        return List.of(a, b);
    }
}
