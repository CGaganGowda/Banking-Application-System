package com.Bank.app.service;

import com.Bank.app.mapper.*;
import com.Bank.app.exception.*;
import com.Bank.app.dto.*;
import com.Bank.app.model.*;
import com.Bank.app.repo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    // ── createAccount ────────────────────────────────────────────

    @DisplayName("Create Account")
    @Test
    void createAccount_validInput_returnsAccountDto() {
        AccountDto inputDto     = buildAccountDto(null, "Gagan", 5000.0);
        Account    mapped       = buildAccount(null, "Gagan", 5000.0);
        Account    saved        = buildAccount(1L, "Gagan", 5000.0);
        AccountDto expectedDto  = buildAccountDto(1L, "Gagan", 5000.0);

        when(accountMapper.toAccount(inputDto)).thenReturn(mapped);
        when(accountRepository.save(mapped)).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(expectedDto);

        AccountDto result = accountService.createAccount(inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Gagan");
        assertThat(result.getBalance()).isEqualTo(5000.0);
        verify(accountRepository).save(mapped);
    }

    // ── getAccountById ───────────────────────────────────────────

    @Test
    void getAccountById_validId_returnsAccountDto() {
        Account    account     = buildAccount(1L, "Gagan", 5000.0);
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", 5000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(expectedDto);

        AccountDto result = accountService.getAccountById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccountById_invalidId_throwsIdNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> accountService.getAccountById(99L));
    }

    // ── deposit ──────────────────────────────────────────────────

    @Test
    void deposit_validAmount_returnsUpdatedAccountDto() {
        Account    account     = buildAccount(1L, "Gagan", 1000.0);
        Account    saved       = buildAccount(1L, "Gagan", 5000.0);
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", 5000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(expectedDto);

        AccountDto result = accountService.deposit(1L, 4000.0);

        assertThat(result.getBalance()).isEqualTo(5000.0);
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class)); // ← transaction must be saved too
    }

    @Test
    void deposit_invalidId_throwsIdNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> accountService.deposit(99L, 4000.0));

        verify(transactionRepository, never()).save(any()); // ← no transaction on failure
    }

    // ── withdraw ─────────────────────────────────────────────────

    @Test
    void withdraw_validAmount_returnsUpdatedAccountDto() {
        Account    account     = buildAccount(1L, "Gagan", 5000.0);
        Account    saved       = buildAccount(1L, "Gagan", 1000.0);
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", 1000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(expectedDto);

        AccountDto result = accountService.withdraw(1L, 4000.0);

        assertThat(result.getBalance()).isEqualTo(1000.0);
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_insufficientFunds_throwsRuntimeException() {
        Account account = buildAccount(1L, "Gagan", 500.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(RuntimeException.class,
                () -> accountService.withdraw(1L, 4000.0));

        verify(accountRepository, never()).save(any());      // ← account not saved
        verify(transactionRepository, never()).save(any());  // ← transaction not saved
    }

    // ── getAllAccounts ────────────────────────────────────────────

    @Test
    void getAllAccounts_returnsListOfAccountDto() {
        Account    acc1 = buildAccount(1L, "Gagan",  5000.0);
        Account    acc2 = buildAccount(2L, "Rakesh", 8000.0);
        AccountDto dto1 = buildAccountDto(1L, "Gagan",  5000.0);
        AccountDto dto2 = buildAccountDto(2L, "Rakesh", 8000.0);

        when(accountRepository.findAll()).thenReturn(List.of(acc1, acc2));
        when(accountMapper.toAccountDto(acc1)).thenReturn(dto1);
        when(accountMapper.toAccountDto(acc2)).thenReturn(dto2);

        List<AccountDto> result = accountService.getAllAccounts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Gagan");
        assertThat(result.get(1).getName()).isEqualTo("Rakesh");
    }

    // ── deleteAccountById ────────────────────────────────────────

    @Test
    void deleteAccountById_validId_deletesSuccessfully() {
        Account account = buildAccount(1L, "Gagan", 5000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.deleteAccountById(1L);

        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccountById_invalidId_throwsIdNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> accountService.deleteAccountById(99L));

        verify(accountRepository, never()).delete(any());
    }

    // ── transferFunds ────────────────────────────────────────────

    @Test
    void transferFunds_validAccounts_transfersSuccessfully() {
        Account fromAcc = buildAccount(1L, "Gagan",  5000.0);
        Account toAcc   = buildAccount(2L, "Rakesh", 1000.0);
        TransferFundsDto dto = new TransferFundsDto(1L, 2L, 2000.0);

        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAcc));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAcc));

        accountService.transferFunds(dto);

        assertThat(fromAcc.getBalance()).isEqualTo(3000.0);  // 5000 - 2000
        assertThat(toAcc.getBalance()).isEqualTo(3000.0);    // 1000 + 2000
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferFunds_insufficientFunds_throwsInsufficientFundsException() {
        Account fromAcc = buildAccount(1L, "Gagan", 500.0);
        Account toAcc   = buildAccount(2L, "Rakesh", 1000.0);
        TransferFundsDto dto = new TransferFundsDto(1L, 2L, 2000.0);

        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAcc));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAcc));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.transferFunds(dto));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    // ── getAllTransactions ────────────────────────────────────────

    @Test
    void getAllTransactions_validAccountId_returnsTransactionDtoList() {
        Transaction    t1   = buildTransaction(1L, 1L, 5000.0, "DEPOSIT");
        Transaction    t2   = buildTransaction(2L, 1L, 2000.0, "WITHDRAW");
        TransactionDto dto1 = new TransactionDto(1L, 1L, 5000.0, "DEPOSIT", LocalDateTime.now());
        TransactionDto dto2 = new TransactionDto(2L, 1L, 2000.0, "WITHDRAW", LocalDateTime.now());

        when(transactionRepository.findByAccountIdOrderByTimestampDesc(1L))
                .thenReturn(List.of(t1, t2));
        when(transactionMapper.toTransactionDto(t1)).thenReturn(dto1);
        when(transactionMapper.toTransactionDto(t2)).thenReturn(dto2);

        List<TransactionDto> result = accountService.getAllTransactions(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTransactionType()).isEqualTo("DEPOSIT");
        assertThat(result.get(1).getTransactionType()).isEqualTo("WITHDRAW");
    }

    // ── helpers ──────────────────────────────────────────────────

    private Account buildAccount(Long id, String name, double balance) {
        Account account = new Account();
        account.setId(id);
        account.setName(name);
        account.setBalance(balance);
        return account;
    }

    private AccountDto buildAccountDto(Long id, String name, double balance) {
        return new AccountDto(id, name, balance);
    }

    private Transaction buildTransaction(Long id, Long accountId, double amount, String type) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setAccountId(accountId);
        t.setAmount(amount);
        t.setTransactionType(type);
        t.setTimestamp(LocalDateTime.now());
        return t;
    }
}