package com.Bank.app.mapper;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.model.Account;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    private Long id;
    private String name;
    private double balance;


    public AccountDto toAccountDto(Account account) {
        AccountDto  accountDto = new AccountDto(
                account.getId(),
                account.getName(),
                account.getBalance()
        );
        return accountDto;
    }

    public Account toAccount(AccountDto accountDto) {
        Account account = new Account(
                accountDto.id(),
                accountDto.name(),
                accountDto.balance()
        );
        return account;
    }



}
