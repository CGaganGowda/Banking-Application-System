package com.Bank.app.dto;

public record TransferFundsDto(Long fromAccountID, Long toAccountID, double amount) {
}
