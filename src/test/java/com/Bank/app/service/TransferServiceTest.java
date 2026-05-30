package com.Bank.app.service;

import com.Bank.app.dto.TransferFundsDto;
import com.Bank.app.exception.IdNotFoundException;
import com.Bank.app.exception.InsufficientFundsException;
import com.Bank.app.model.Account;
import com.Bank.app.repo.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void transferFunds_validRequest_updatesBothAccounts() {
        Account sender   = buildAccount(1L, "Gagan", BigDecimal.valueOf(2000));
        Account receiver = buildAccount(2L, "Arjun", BigDecimal.valueOf(500));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        accountService.transferFunds(new TransferFundsDto(1L, 2L, BigDecimal.valueOf(1000)));

        assertThat(sender.getBalance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(receiver.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transferFunds_insufficientBalance_throwsAndDoesNotSave() {
        Account sender   = buildAccount(1L, "Gagan", BigDecimal.valueOf(100));
        Account receiver = buildAccount(2L, "Arjun", BigDecimal.valueOf(500));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));

        assertThatThrownBy(() ->
                accountService.transferFunds(new TransferFundsDto(1L, 2L, BigDecimal.valueOf(800))))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountRepository, never()).save(any());
    }

    @Test
    void transferFunds_senderNotFound_throwsIdNotFoundException() {
        Account receiver = buildAccount(2L, "Arjun", BigDecimal.valueOf(500));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                accountService.transferFunds(new TransferFundsDto(99L, 2L, BigDecimal.valueOf(500))))
                .isInstanceOf(IdNotFoundException.class);
    }

    @Test
    void transferFunds_exactBalance_succeeds() {
        Account sender   = buildAccount(1L, "Gagan", BigDecimal.valueOf(500));
        Account receiver = buildAccount(2L, "Arjun", BigDecimal.valueOf(0));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        accountService.transferFunds(new TransferFundsDto(1L, 2L, BigDecimal.valueOf(500)));

        assertThat(sender.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        assertThat(receiver.getBalance()).isEqualTo(BigDecimal.valueOf(500));
    }

    private Account buildAccount(Long id, String name, BigDecimal balance) {
        Account a = new Account();
        a.setId(id);
        a.setName(name);
        a.setBalance(balance);
        return a;
    }
}