package com.notificationService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper ;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferNotificationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transfer.completed", groupId = "notification-service-group")
    public void onTransferCompleted(@Payload byte[] payload) throws Exception {
        Map<String, Object> event = objectMapper.readValue(payload, Map.class);

        String transferId = event.get("transferId").toString();
        String status = event.get("status").toString();


        log.info("✅ [NOTIFICATION] Transferência concluída!");
        log.info("   TransferId : {}", transferId);
        log.info("   Status     : {}", status);
        log.info("   [EMAIL] Notificação enviada ao remetente e destinatário");
    }

    @KafkaListener(topics = "transfer.failed", groupId = "notification-service-group")
    public void onTransferFailed(@Payload byte[] payload) throws Exception {
        Map<String, Object> event = objectMapper.readValue(payload, Map.class);

        String transferId = event.get("transferId").toString();
        String reason = event.get("reason").toString();

        log.warn("❌ [NOTIFICATION] Transferência falhou!");
        log.warn("   TransferId : {}", transferId);
        log.warn("   Motivo     : {}", reason);
        log.warn("   [EMAIL] Notificação de falha enviada ao remetente");
    }
}