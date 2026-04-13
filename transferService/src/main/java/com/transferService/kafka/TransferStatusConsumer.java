package com.transferService.kafka;

import com.transferService.enums.StatusTransfer;
import com.transferService.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferStatusConsumer {

    private final TransferRepository transferRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transfer.completed", groupId = "transfer-service-group")
    public void onTransferCompleted(@Payload byte[] payload) throws Exception {
        Map<String, Object> event = objectMapper.readValue(payload, Map.class);
        UUID transferId = UUID.fromString(event.get("transferId").toString());
        updateStatus(transferId, StatusTransfer.COMPLETED);
    }

    @KafkaListener(topics = "transfer.failed", groupId = "transfer-service-group")
    public void onTransferFailed(@Payload byte[] payload) throws Exception {
        Map<String, Object> event = objectMapper.readValue(payload, Map.class);
        UUID transferId = UUID.fromString(event.get("transferId").toString());
        updateStatus(transferId, StatusTransfer.FAILED);
    }

    private void updateStatus(UUID transferId, StatusTransfer status) {
        transferRepository.findById(transferId).ifPresent(transfer -> {
            transfer.setStatus(status);
            transferRepository.save(transfer);
        });
    }
}