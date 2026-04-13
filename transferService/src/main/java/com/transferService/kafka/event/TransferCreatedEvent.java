package com.transferService.kafka.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCreatedEvent(
        UUID transferId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount
) {}