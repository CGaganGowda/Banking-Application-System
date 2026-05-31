package com.Bank.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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

    @Positive
    @Min(0)
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime timestamp;
}
