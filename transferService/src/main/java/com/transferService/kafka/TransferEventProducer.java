package com.transferService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transferService.entity.Transfer;
import com.transferService.kafka.event.TransferCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class TransferEventProducer {

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTransferCreated(Transfer transfer) {
        String topic = "transfer.created";
        String key = transfer.getId().toString();

        TransferCreatedEvent event = new TransferCreatedEvent(transfer.getId(), transfer.getSourceAccountId(), transfer.getDestinationAccountId(), transfer.getAmount());
        byte[] payload = objectMapper.writeValueAsBytes(event);
        kafkaTemplate.send(topic, key, payload);
    }


}
