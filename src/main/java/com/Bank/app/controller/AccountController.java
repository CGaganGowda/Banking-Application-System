package com.Bank.app.controller;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;
import com.Bank.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;



    @PostMapping("")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountDto> getAccountId(@PathVariable Long id){
        return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
    }

    @PutMapping("{id}/deposit")
    public ResponseEntity<AccountDto> depositMoney(@PathVariable Long id,@RequestBody Map<String,Double> map){
        double amount = map.get("amount");
        return new ResponseEntity<>(accountService.deposit(id,amount),HttpStatus.OK);
    }

    @PutMapping("{id}/withdraw")
    public ResponseEntity<AccountDto> withdrawMoney(@PathVariable Long id,@RequestBody Map<String,Double> map){
        double amount = map.get("amount");
        return new ResponseEntity<>(accountService.withdraw(id,amount),HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<AccountDto>> getAllAccounts(){
        return new ResponseEntity<>(accountService.getAllAccounts(),HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id){
        accountService.deleteAccountById(id);
        return new ResponseEntity<>("Account Deleted with id : "+id, HttpStatus.OK);
    }

    @PostMapping("transfer")
    public ResponseEntity<String> transferAmount(@RequestBody TransferFundsDto transferFundsDto){
        accountService.transferFunds(transferFundsDto);
        return new ResponseEntity<>("Amount is successfully transferred",HttpStatus.OK);
    }

    @GetMapping("{id}/transactions")
    public List<TransactionDto> getAllTransactionsByAccountId(@PathVariable Long id){
        return accountService.getAllTransactions(id);
    }


}

