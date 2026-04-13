package com.transferService.service;

import com.transferService.dto.TransferRequest;
import com.transferService.entity.Transfer;
import com.transferService.enums.StatusTransfer;
import com.transferService.kafka.TransferEventProducer;
import com.transferService.kafka.event.TransferCreatedEvent;
import com.transferService.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final RedissonClient redissonClient;
    private final TransferEventProducer transferEventProducer;

    public Transfer createTransfer(TransferRequest request) {
        String lockKey = "lock:account:" + request.sourceAccountId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new RuntimeException("Conta ocupada, tente novamente");
            }

            if (request.sourceAccountId().equals(request.destinationAccountId())){
                throw new RuntimeException("Conta origem e destino não podem ser iguais");
            }

            Transfer transfer = Transfer.builder()
                    .sourceAccountId(request.sourceAccountId())
                    .destinationAccountId(request.destinationAccountId())
                    .amount(request.amount())
                    .description(request.description())
                    .status(StatusTransfer.PENDING)
                    .build();
            Transfer saved = transferRepository.save(transfer);
            transferEventProducer.publishTransferCreated(saved);
            return saved;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transferência interrompida");
        } finally {
            // sempre libera o lock
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public Transfer getTransfer(UUID id){
        return transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
    }

}
