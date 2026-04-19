package com.Bank.app.service;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.entity.Account;
import com.Bank.app.exception.AccountNotFoundException;
import com.Bank.app.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    // ── create account ──────────────────────────────────────────

    @Test
    void createAccount_validInput_returnsAccountDto() {
        Account account = buildAccount(1L, "Gagan Gowda", 5000.0);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.createAccount(
                new AccountDto(null, "Gagan Gowda", 5000.0));

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAccountHolderName()).isEqualTo("Gagan Gowda");
        assertThat(result.getBalance()).isEqualTo(5000.0);
        verify(accountRepository).save(any(Account.class));
    }

    // ── get account by id ────────────────────────────────────────

    @Test
    void getAccountById_existingId_returnsAccountDto() {
        Account account = buildAccount(1L, "Gagan Gowda", 5000.0);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccountById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAccountHolderName()).isEqualTo("Gagan Gowda");
    }

    @Test
    void getAccountById_nonExistingId_throwsAccountNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(99L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── deposit ──────────────────────────────────────────────────

    @Test
    void deposit_validAmount_increasesBalance() {
        Account account = buildAccount(1L, "Gagan Gowda", 1000.0);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.deposit(1L, 500.0);

        assertThat(result.getBalance()).isEqualTo(1500.0);
    }

    // ── withdraw ─────────────────────────────────────────────────

    @Test
    void withdraw_sufficientBalance_decreasesBalance() {
        Account account = buildAccount(1L, "Gagan Gowda", 1000.0);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDto result = accountService.withdraw(1L, 300.0);

        assertThat(result.getBalance()).isEqualTo(700.0);
    }

    @Test
    void withdraw_insufficientBalance_throwsException() {
        Account account = buildAccount(1L, "Gagan Gowda", 200.0);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.withdraw(1L, 500.0))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(accountRepository, never()).save(any());
    }

    // ── helper ───────────────────────────────────────────────────

    private Account buildAccount(Long id, String name, double balance) {
        Account a = new Account();
        a.setId(id);
        a.setAccountHolderName(name);
        a.setBalance(balance);
        return a;
    }
}
