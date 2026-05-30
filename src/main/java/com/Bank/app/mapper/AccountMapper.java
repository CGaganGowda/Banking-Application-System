package com.Bank.app.mapper;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.model.Account;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDto toAccountDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getName(),
                account.getBalance()
        );
    }

    public Account toAccount(AccountDto accountDto) {
        return new Account(
                accountDto.getId(),
                accountDto.getName(),
                accountDto.getBalance()
        );
    }



}
