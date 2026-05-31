package com.Bank.app.service;


import com.Bank.app.mapper.*;
import com.Bank.app.exception.*;
import com.Bank.app.dto.*;
import com.Bank.app.model.*;
import com.Bank.app.repo.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {


    private AccountMapper accountMapper;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionMapper transactionMapper;

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";


    //Create an Account
    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.toAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );
        return accountMapper.toAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );
        BigDecimal total = account.getBalance().add(amount);
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);

        transactionRepository.save(transaction);

        return accountMapper.toAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );

        if(amount.compareTo(account.getBalance())>0){
            throw new InsufficientFundsException("Insufficient funds");
        }

        BigDecimal total = account.getBalance().subtract(amount);
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);

        transactionRepository.save(transaction);

        return  accountMapper.toAccountDto(savedAccount);
    }

    @Override
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        Page<Account> accountList =  accountRepository.findAll(pageable);
         return accountList.map(accountMapper::toAccountDto);
    }

    @Override
    public void deleteAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );
        accountRepository.delete(account);
    }

    @Transactional
    @Override
    public void transferFunds(TransferFundsDto transferFundsDto) {

        LocalDateTime timestamp = LocalDateTime.now();

        if(transferFundsDto.getToAccountId().equals(transferFundsDto.getFromAccountId())){
            throw new IllegalArgumentException("Source and Destination accounts should not be same");
        }

        if(transferFundsDto.getAmount().compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }


        Account toAccount = accountRepository.findById(
                transferFundsDto.getToAccountId()).orElseThrow(
                () -> new IdNotFoundException("Account with id: " +transferFundsDto.getToAccountId()+ " not found"));
        Account fromAccount = accountRepository.findById(
                transferFundsDto.getFromAccountId()).orElseThrow(
                () -> new IdNotFoundException("Account with id: " +transferFundsDto.getFromAccountId()+ " not found"));


        if(fromAccount.getBalance().compareTo(transferFundsDto.getAmount())<0){
            throw new InsufficientFundsException("Insufficient funds");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferFundsDto.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transferFundsDto.getAmount()));


        Transaction depositTransaction = new Transaction();
        depositTransaction.setAccountId(fromAccount.getId());
        depositTransaction.setAmount(transferFundsDto.getAmount());
        depositTransaction.setTimestamp(timestamp);
        depositTransaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);

        transactionRepository.save(depositTransaction);

        Transaction withdrawTransaction = new Transaction();
        withdrawTransaction.setAccountId(toAccount.getId());
        withdrawTransaction.setAmount(transferFundsDto.getAmount());
        withdrawTransaction.setTimestamp(timestamp);
        withdrawTransaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);

        transactionRepository.save(withdrawTransaction);
    }


    @Override
    public Page<TransactionDto> getAllTransactions(Long accountId,Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(accountId,pageable);
        return transactions.map(transactionMapper::toTransactionDto);

    }

}


