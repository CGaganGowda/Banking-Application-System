package com.Bank.app.service;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccountById(Long id);
    AccountDto deposit(Long id, double amount);
    AccountDto withdraw(Long id, double amount);
    List<AccountDto> getAllAccounts();
    void deleteAccountById(Long id);
    void transferFunds(TransferFundsDto transferFundsDto);
    List<TransactionDto> getAllTransactions(Long AccountId);
}
