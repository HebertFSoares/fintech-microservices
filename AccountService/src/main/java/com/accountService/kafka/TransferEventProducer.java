package com.accountService.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferEventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishTransferCompleted(UUID transferId) {
        publish("transfer.completed", transferId, Map.of("transferId", transferId, "status", "COMPLETED"));
    }

    public void publishTransferFailed(UUID transferId, String reason) {
        publish("transfer.failed", transferId, Map.of("transferId", transferId, "status", "FAILED", "reason", reason));
    }

    private void publish(String topic, UUID key, Map<String, Object> payload) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(payload);
            kafkaTemplate.send(topic, key.toString(), bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }
}