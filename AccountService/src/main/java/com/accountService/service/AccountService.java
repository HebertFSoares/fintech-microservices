package com.accountService.service;

import com.accountService.entity.Account;
import com.accountService.enums.AccountStatus;
import com.accountService.enums.AccountType;
import com.accountService.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(UUID userId) {

        if (accountRepository.existsByUserId(userId)) {
            return accountRepository.findByUserId(userId)
                    .orElseThrow();
        }

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(generateAccountNumber())
                .accountType(AccountType.CHECKING)
                .accountStatus(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        long number = ThreadLocalRandom.current()
                .nextLong(1000000000L, 9999999999L);

        return String.valueOf(number);
    }

    public Account getAccount (UUID id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public BigDecimal getBalance(UUID id){
        return getAccount(id).getBalance();
    }

    public void debit(UUID accountId, BigDecimal amount){
        Account account = getAccount(accountId);

        if (account.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Saldo insuficiente");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    public void credit(UUID accountId, BigDecimal amount){
        Account account = getAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

}
