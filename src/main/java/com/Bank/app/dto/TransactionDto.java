package com.Bank.app.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto{
    private Long id;
    private Long accountId;
    private double amount;
    private String transactionType;
    private LocalDateTime timestamp;
}
