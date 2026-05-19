package com.Bank.app.service;

import com.Bank.app.dto.TransferFundsDto;
import com.Bank.app.exception.IdNotFoundException;
import com.Bank.app.exception.InsufficientFundsException;
import com.Bank.app.mapper.AccountMapper;
import com.Bank.app.mapper.TransactionMapper;
import com.Bank.app.model.Account;
import com.Bank.app.repo.AccountRepository;
import com.Bank.app.repo.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void transferFunds_validRequest_updatesBothAccounts() {
        Account sender    = buildAccount(1L, "Gagan",  2000.0);
        Account receiver  = buildAccount(2L, "Arjun",  500.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(accountRepository.save(sender)).thenReturn(sender);
        when(accountRepository.save(receiver)).thenReturn(receiver);

        accountService.transferFunds(new TransferFundsDto(1L, 2L, 1000.0));

        assertThat(sender.getBalance()).isEqualTo(1000.0);
        assertThat(receiver.getBalance()).isEqualTo(1500.0);
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transferFunds_insufficientBalance_throwsAndDoesNotSave() {
        Account sender   = buildAccount(1L, "Gagan", 100.0);
        Account receiver = buildAccount(2L, "Arjun", 500.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThatThrownBy(() ->
                accountService.transferFunds(new TransferFundsDto(1L, 2L, 800.0)))
                .isInstanceOf(InsufficientFundsException.class);

        // neither account should have been saved
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transferFunds_senderNotFound_throwsAccountNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                accountService.transferFunds(new TransferFundsDto(99L, 2L, 500.0)))
                .isInstanceOf(IdNotFoundException.class);
    }

    @Test
    void transferFunds_exactBalance_succeeds() {
        Account sender   = buildAccount(1L, "Gagan", 500.0);
        Account receiver = buildAccount(2L, "Arjun", 0.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        accountService.transferFunds(new TransferFundsDto(1L, 2L, 500.0));

        assertThat(sender.getBalance()).isEqualTo(0.0);
        assertThat(receiver.getBalance()).isEqualTo(500.0);
    }

    private Account buildAccount(Long id, String name, double balance) {
        Account a = new Account();
        a.setId(id);
        a.setName(name);
        a.setBalance(balance);
        return a;
    }
}
