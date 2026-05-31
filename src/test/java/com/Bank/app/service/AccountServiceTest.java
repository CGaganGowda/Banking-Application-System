package com.Bank.app.service;

import com.Bank.app.mapper.AccountMapper;
import com.Bank.app.mapper.TransactionMapper;
import com.Bank.app.exception.IdNotFoundException;
import com.Bank.app.exception.InsufficientFundsException;
import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;
import com.Bank.app.model.Account;
import com.Bank.app.model.Transaction;
import com.Bank.app.repo.AccountRepository;
import com.Bank.app.repo.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
        AccountDto inputDto    = buildAccountDto(null, "Gagan", BigDecimal.valueOf(5000));
        Account    mapped      = buildAccount(null, "Gagan", BigDecimal.valueOf(5000));
        Account    saved       = buildAccount(1L, "Gagan", BigDecimal.valueOf(5000));
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", BigDecimal.valueOf(5000));

        when(accountMapper.toAccount(inputDto)).thenReturn(mapped);
        when(accountRepository.save(mapped)).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(expectedDto);

        AccountDto result = accountService.createAccount(inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Gagan");
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(5000));
        verify(accountRepository).save(mapped);
    }

    // ── getAccountById ───────────────────────────────────────────

    @Test
    void getAccountById_validId_returnsAccountDto() {
        Account    account     = buildAccount(1L, "Gagan", BigDecimal.valueOf(5000));
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", BigDecimal.valueOf(5000));

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
        Account    account     = buildAccount(1L, "Gagan", BigDecimal.valueOf(1000));
        Account    saved       = buildAccount(1L, "Gagan", BigDecimal.valueOf(5000));
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", BigDecimal.valueOf(5000));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(expectedDto);

        AccountDto result = accountService.deposit(1L, BigDecimal.valueOf(4000));

        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(5000));
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_invalidId_throwsIdNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> accountService.deposit(99L, BigDecimal.valueOf(4000)));

        verify(transactionRepository, never()).save(any());
    }

    // ── withdraw ─────────────────────────────────────────────────

    @Test
    void withdraw_validAmount_returnsUpdatedAccountDto() {
        Account    account     = buildAccount(1L, "Gagan", BigDecimal.valueOf(5000));
        Account    saved       = buildAccount(1L, "Gagan", BigDecimal.valueOf(1000));
        AccountDto expectedDto = buildAccountDto(1L, "Gagan", BigDecimal.valueOf(1000));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(expectedDto);

        AccountDto result = accountService.withdraw(1L, BigDecimal.valueOf(4000));

        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(1000));
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_insufficientFunds_throwsInsufficientFundsException() {
        Account account = buildAccount(1L, "Gagan", BigDecimal.valueOf(500));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.withdraw(1L, BigDecimal.valueOf(4000)));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    // ── getAllAccounts ────────────────────────────────────────────

    @Test
    void getAllAccounts_returnsListOfAccountDto() {
        Account    acc1 = buildAccount(1L, "Gagan",  BigDecimal.valueOf(5000));
        Account    acc2 = buildAccount(2L, "Rakesh", BigDecimal.valueOf(8000));
        AccountDto dto1 = buildAccountDto(1L, "Gagan",  BigDecimal.valueOf(5000));
        AccountDto dto2 = buildAccountDto(2L, "Rakesh", BigDecimal.valueOf(8000));

        Pageable pageable = PageRequest.of(0,10);
        Page<Account> accountPage = new PageImpl<>(List.of(acc1,acc2),pageable,2);

        when(accountRepository.findAll(pageable)).thenReturn(accountPage);
        when(accountMapper.toAccountDto(acc1)).thenReturn(dto1);
        when(accountMapper.toAccountDto(acc2)).thenReturn(dto2);

        Page<AccountDto> result = accountService.getAllAccounts(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Gagan");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Rakesh");
        verify(accountRepository).findAll(pageable);
    }

    // ── deleteAccountById ────────────────────────────────────────

    @Test
    void deleteAccountById_validId_deletesSuccessfully() {
        Account account = buildAccount(1L, "Gagan", BigDecimal.valueOf(5000));

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
        Account fromAcc = buildAccount(1L, "Gagan",  BigDecimal.valueOf(5000));
        Account toAcc   = buildAccount(2L, "Rakesh", BigDecimal.valueOf(1000));
        TransferFundsDto dto = new TransferFundsDto(1L, 2L, BigDecimal.valueOf(2000));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAcc));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAcc));

        accountService.transferFunds(dto);

        assertThat(fromAcc.getBalance()).isEqualTo(BigDecimal.valueOf(3000));
        assertThat(toAcc.getBalance()).isEqualTo(BigDecimal.valueOf(3000));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferFunds_insufficientFunds_throwsInsufficientFundsException() {
        Account fromAcc = buildAccount(1L, "Gagan", BigDecimal.valueOf(500));
        Account toAcc   = buildAccount(2L, "Rakesh", BigDecimal.valueOf(1000));
        TransferFundsDto dto = new TransferFundsDto(1L, 2L, BigDecimal.valueOf(2000));

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
        Transaction    t1   = buildTransaction(1L, 1L, BigDecimal.valueOf(5000), "DEPOSIT");
        Transaction    t2   = buildTransaction(2L, 1L, BigDecimal.valueOf(2000), "WITHDRAW");
        TransactionDto dto1 = new TransactionDto(1L, 1L, BigDecimal.valueOf(5000), "DEPOSIT",  LocalDateTime.now());
        TransactionDto dto2 = new TransactionDto(2L, 1L, BigDecimal.valueOf(2000), "WITHDRAW", LocalDateTime.now());

        Pageable pageable = PageRequest.of(0,10);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(t1,t2),pageable,2);

        when(transactionRepository.findByAccountIdOrderByTimestampDesc(1L,pageable))
                .thenReturn(transactionPage);
        when(transactionMapper.toTransactionDto(t1)).thenReturn(dto1);
        when(transactionMapper.toTransactionDto(t2)).thenReturn(dto2);

        Page<TransactionDto> result = accountService.getAllTransactions(1L,pageable);

        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).getTransactionType()).isEqualTo("DEPOSIT");
        assertThat(result.getContent().get(1).getTransactionType()).isEqualTo("WITHDRAW");
        verify(transactionRepository, times(1)).findByAccountIdOrderByTimestampDesc(1L,pageable);
    }

    // ── helpers ──────────────────────────────────────────────────

    private Account buildAccount(Long id, String name, BigDecimal balance) {
        Account account = new Account();
        account.setId(id);
        account.setName(name);
        account.setBalance(balance);
        return account;
    }

    private AccountDto buildAccountDto(Long id, String name, BigDecimal balance) {
        return new AccountDto(id, name, balance);
    }

    private Transaction buildTransaction(Long id, Long accountId, BigDecimal amount, String type) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setAccountId(accountId);
        t.setAmount(amount);
        t.setTransactionType(type);
        t.setTimestamp(LocalDateTime.now());
        return t;
    }
}