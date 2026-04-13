package com.transferService.dto;

import com.transferService.enums.StatusTransfer;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(UUID id, UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, String description, StatusTransfer status) {
}
