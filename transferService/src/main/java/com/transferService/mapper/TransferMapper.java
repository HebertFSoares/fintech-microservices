package com.transferService.mapper;

import com.transferService.dto.TransferResponse;
import com.transferService.entity.Transfer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    TransferResponse toResponse(Transfer transfer);
}
