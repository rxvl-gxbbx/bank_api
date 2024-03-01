package com.rxvlvxr.bank.repositories;

import com.rxvlvxr.bank.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAllByBirthDateAfter(LocalDate birthDate);

    List<User> findAllByFullNameStartingWith(String like);
}
