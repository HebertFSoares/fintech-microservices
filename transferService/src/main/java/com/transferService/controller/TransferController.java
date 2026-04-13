package com.transferService.controller;

import com.transferService.dto.TransferRequest;
import com.transferService.dto.TransferResponse;
import com.transferService.entity.Transfer;
import com.transferService.mapper.TransferMapper;
import com.transferService.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final TransferMapper transferMapper;

    @PostMapping
    public ResponseEntity<TransferResponse> create(@RequestBody TransferRequest request){
        Transfer transfer = transferService.createTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferMapper.toResponse(transfer));
    }
}
