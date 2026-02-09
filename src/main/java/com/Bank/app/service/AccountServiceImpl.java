package com.Bank.app.service;


import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;
import com.Bank.app.exception.IdNotFoundException;
import com.Bank.app.exception.InsufficientFundsException;
import com.Bank.app.mapper.AccountMapper;
import com.Bank.app.mapper.TransactionMapper;
import com.Bank.app.model.Account;
import com.Bank.app.model.Transaction;
import com.Bank.app.repo.AccountRepository;
import com.Bank.app.repo.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";

//    public AccountServiceImpl(AccountMapper accountMapper) {
//        this.accountMapper = accountMapper;
//    }

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
            throw new RuntimeException("Insufficient funds");
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
                transferFundsDto.toAccountID()).orElseThrow(
                () -> new IdNotFoundException("Account with id: " +transferFundsDto.toAccountID()+ " not found"));
        Account froAcc = accountRepository.findById(
                transferFundsDto.fromAccountID()).orElseThrow(
                () -> new IdNotFoundException("Account with id: " +transferFundsDto.fromAccountID()+ " not found"));

        if(froAcc.getBalance() < transferFundsDto.amount()){
            throw new InsufficientFundsException("Insufficient funds");
        }
        froAcc.setBalance(froAcc.getBalance()-transferFundsDto.amount());
        toAcc.setBalance(toAcc.getBalance()+transferFundsDto.amount());

        accountRepository.save(toAcc);
        accountRepository.save(froAcc);

        Transaction DepoTransaction = new Transaction();
        DepoTransaction.setAccountId(froAcc.getId());
        DepoTransaction.setAmount(transferFundsDto.amount());
        DepoTransaction.setTimestamp(LocalDateTime.now());
        DepoTransaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);

        transactionRepository.save(DepoTransaction);

        Transaction WithTransaction = new Transaction();
        WithTransaction.setAccountId(toAcc.getId());
        WithTransaction.setAmount(transferFundsDto.amount());
        WithTransaction.setTimestamp(LocalDateTime.now());
        WithTransaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);

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
