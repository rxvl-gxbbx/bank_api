package com.rxvlvxr.bank.repositories;

import com.rxvlvxr.bank.models.Account;
import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull
    List<Account> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findById(long id);
}
