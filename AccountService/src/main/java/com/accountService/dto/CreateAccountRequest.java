package com.accountService.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateAccountRequest{
    private UUID userId;
}
