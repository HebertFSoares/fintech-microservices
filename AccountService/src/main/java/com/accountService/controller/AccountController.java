package com.accountService.controller;

import com.accountService.dto.AccountResponse;
import com.accountService.dto.BalanceResponse;
import com.accountService.dto.CreateAccountRequest;
import com.accountService.entity.Account;
import com.accountService.mapper.AccountMapper;
import com.accountService.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest createRequest){
        Account accountCreate = accountService.createAccount(createRequest.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountMapper.toResponse(accountCreate)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id){
        Account getAccount = accountService.getAccount(id);
        return ResponseEntity.ok(accountMapper.toResponse(getAccount));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID id){
        BigDecimal balance = accountService.getBalance(id);
        BalanceResponse response = new BalanceResponse(id, balance);
        return ResponseEntity.ok(response);
    }
}
