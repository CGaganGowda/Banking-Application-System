package com.Bank.app.service;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccountById(Long id);
    AccountDto deposit(Long id, BigDecimal amount);
    AccountDto withdraw(Long id, BigDecimal amount);
    Page<AccountDto> getAllAccounts(Pageable pageable);
    void deleteAccountById(Long id);
    void transferFunds(TransferFundsDto transferFundsDto);
    Page<TransactionDto> getAllTransactions(Long AccountId,Pageable pageable);
}
