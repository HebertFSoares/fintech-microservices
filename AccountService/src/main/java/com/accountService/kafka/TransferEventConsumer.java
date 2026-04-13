package com.accountService.kafka;

import com.accountService.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferEventConsumer {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final TransferEventProducer transferEventProducer;

    @KafkaListener(topics = "transfer.created", groupId = "account-service-group")
    public void onTransferCreated(@Payload byte[] payload) throws Exception {
        Map<String, Object> event = objectMapper.readValue(payload, Map.class);

        UUID transferId = UUID.fromString(event.get("transferId").toString());
        UUID sourceAccountId = UUID.fromString(event.get("sourceAccountId").toString());
        UUID destinationAccountId = UUID.fromString(event.get("destinationAccountId").toString());
        BigDecimal amount = new BigDecimal(event.get("amount").toString());

        try {
            accountService.debit(sourceAccountId, amount);
            accountService.credit(destinationAccountId, amount);
            transferEventProducer.publishTransferCompleted(transferId);
        } catch (Exception e) {
            transferEventProducer.publishTransferFailed(transferId, e.getMessage());
        }
    }

}
