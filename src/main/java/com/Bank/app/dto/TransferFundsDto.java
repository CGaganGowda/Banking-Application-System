package com.Bank.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferFundsDto{
    private Long fromAccountId;
    private Long toAccountId;

    @Positive
    @Min(0)
    private BigDecimal amount;
}
