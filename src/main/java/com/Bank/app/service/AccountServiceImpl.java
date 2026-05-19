package com.Bank.app.service;


import com.Bank.app.mapper.*;
import com.Bank.app.exception.*;
import com.Bank.app.dto.*;
import com.Bank.app.model.*;
import com.Bank.app.repo.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {


    private AccountMapper accountMapper;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionMapper transactionMapper;

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";

//    public AccountServiceImpl(AccountMapper accountMapper) {
//        this.accountMapper = accountMapper;
//    }

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
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );
        double total = account.getBalance() + amount;
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
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );

        if(amount > account.getBalance()){
            throw new InsufficientFundsException("Insufficient funds");
        }

        double total = account.getBalance() - amount;
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
    public List<AccountDto> getAllAccounts() {
        List<Account> accountList =  accountRepository.findAll();
         return accountList.stream()
                //.map((Account) -> accountMapper.toAccountDto(Account))
                 .map(accountMapper::toAccountDto)
                .toList();
    }

    @Override
    public void deleteAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Account with id: " + id + " not found")
        );
        accountRepository.delete(account);
    }

    @Override
    public void transferFunds(TransferFundsDto transferFundsDto) {
        Account toAcc = accountRepository.findById(
                transferFundsDto.getToAccountId()).orElseThrow(
                () -> new IdNotFoundException("Account with id: " +transferFundsDto.getToAccountId()+ " not found"));
        Account froAcc = accountRepository.findById(
                transferFundsDto.getFromAccountId()).orElseThrow(
                () -> new IdNotFoundException("Account with id: " +transferFundsDto.getFromAccountId()+ " not found"));

        if(froAcc.getBalance() < transferFundsDto.getAmount()){
            throw new InsufficientFundsException("Insufficient funds");
        }
        froAcc.setBalance(froAcc.getBalance()-transferFundsDto.getAmount());
        toAcc.setBalance(toAcc.getBalance()+transferFundsDto.getAmount());

        accountRepository.save(toAcc);
        accountRepository.save(froAcc);

        Transaction DepoTransaction = new Transaction();
        DepoTransaction.setAccountId(froAcc.getId());
        DepoTransaction.setAmount(transferFundsDto.getAmount());
        DepoTransaction.setTimestamp(LocalDateTime.now());
        DepoTransaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);

        transactionRepository.save(DepoTransaction);

        Transaction WithTransaction = new Transaction();
        WithTransaction.setAccountId(toAcc.getId());
        WithTransaction.setAmount(transferFundsDto.getAmount());
        WithTransaction.setTimestamp(LocalDateTime.now());
        WithTransaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);

        transactionRepository.save(WithTransaction);
    }


    @Override
    public List<TransactionDto> getAllTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
        List<TransactionDto> collect = transactions.stream()
                .map((transactionMapper::toTransactionDto))
                .collect(Collectors.toList());

        return collect;
    }

}


