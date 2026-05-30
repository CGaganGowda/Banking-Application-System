package com.Bank.app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto{
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime timestamp;
}
