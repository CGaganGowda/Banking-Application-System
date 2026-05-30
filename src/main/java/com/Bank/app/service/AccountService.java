package com.Bank.app.service;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccountById(Long id);
    AccountDto deposit(Long id, BigDecimal amount);
    AccountDto withdraw(Long id, BigDecimal amount);
    List<AccountDto> getAllAccounts();
    void deleteAccountById(Long id);
    void transferFunds(TransferFundsDto transferFundsDto);
    List<TransactionDto> getAllTransactions(Long AccountId);
}
