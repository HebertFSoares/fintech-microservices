package com.accountService.mapper;

import com.accountService.dto.AccountResponse;
import com.accountService.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toResponse(Account account);
}
