package com.Bank.app.mapper;

import com.Bank.app.dto.TransactionDto;
import com.Bank.app.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    private Long id;
    private Long accountId;
    private double amount;
    private String transactionType;
    private LocalDateTime timestamp;

    public TransactionDto toTransactionDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto(
               transaction.getId(),
               transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp()
        );
        return transactionDto;
    }
}
