package com.rxvlvxr.bank.repositories;

import com.rxvlvxr.bank.models.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByAddress(String address);
}
