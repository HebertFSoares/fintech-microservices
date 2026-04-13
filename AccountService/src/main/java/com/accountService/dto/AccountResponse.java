package com.accountService.dto;

import com.accountService.enums.AccountStatus;
import com.accountService.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        AccountType accountType,
        AccountStatus accountStatus,
        BigDecimal balance
) {
}
