package com.accountService.repository;

import com.accountService.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    boolean existsByUserId(UUID userId);

    Optional<Account> findByUserId(UUID userId);
}