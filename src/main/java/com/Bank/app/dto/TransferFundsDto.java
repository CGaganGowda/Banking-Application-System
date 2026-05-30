package com.Bank.app.dto;

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
    private BigDecimal amount;
}
