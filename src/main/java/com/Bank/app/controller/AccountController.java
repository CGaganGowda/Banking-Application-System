package com.Bank.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import com.Bank.app.dto.AccountDto;
import com.Bank.app.dto.TransactionDto;
import com.Bank.app.dto.TransferFundsDto;
import com.Bank.app.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Tag(
        name = "REST API for Banking System",
        description = "Banking Application API documentation"
)
@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    @Operation(
            summary = "CREATE API",
            description = "Creating a customer"
    )
    @ApiResponse(
            responseCode = "201",
            description = "CUSTOMER CREATED"
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @PostMapping("")
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid AccountDto accountDto) {
        return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.CREATED);
    }


    @Operation(
            summary = "GET CUSTOMER ON ID",
            description = "Provides the details of customer with the given ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Customer with given ID is returned."
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CUSTOMER')")
    @GetMapping("{id}")
    public ResponseEntity<AccountDto> getAccountId(@PathVariable Long id){
        return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
    }


    @Operation(
            summary = "DEPOSIT AMOUNT",
            description = "Deposits the amount in to the customer's account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Amount is deposited in to customer's account and balance is displayed."
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CUSTOMER')")
    @PutMapping("{id}/deposit")
    public ResponseEntity<AccountDto> depositMoney(@PathVariable Long id,@RequestBody Map<String,Double> map){
        BigDecimal amount = BigDecimal.valueOf(map.get("amount"));
        return new ResponseEntity<>(accountService.deposit(id, amount),HttpStatus.OK);
    }


    @Operation(
            summary = "WITHDRAW AMOUNT",
            description = "Withdraws the amount from  the customer's account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Amount is withdrawn from the customer's account and balance is displayed."
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CUSTOMER')")
    @PutMapping("{id}/withdraw")
    public ResponseEntity<AccountDto> withdrawMoney(@PathVariable Long id,@RequestBody Map<String,Double> map){
        double amount = map.get("amount");
        return new ResponseEntity<>(accountService.withdraw(id, BigDecimal.valueOf(amount)),HttpStatus.OK);
    }


    @Operation(
            summary = "GET ALL CUSTOMERS",
            description = "Provides the details of all the customers."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Customers are returned."
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping()
    public ResponseEntity<List<AccountDto>> getAllAccounts(){
        return new ResponseEntity<>(accountService.getAllAccounts(),HttpStatus.OK);
    }



    @Operation(
            summary = "DELETE Customer with given ID",
            description = "Deletes the customer account."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Customer is deleted."
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id){
        accountService.deleteAccountById(id);
        return new ResponseEntity<>("Account Deleted with id : "+id, HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "TRANSFER FUNDS",
            description = "Transfer funds between the valid source and destination customers."
            )
    @ApiResponse(
            responseCode = "200",
            description = "Funds transferred successfully."
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CUSTOMER')")
    @PostMapping("transfer")
    public ResponseEntity<String> transferAmount(@RequestBody @Valid TransferFundsDto transferFundsDto){
        accountService.transferFunds(transferFundsDto);
        return new ResponseEntity<>("Amount is successfully transferred",HttpStatus.OK);
    }

    @Operation(
            summary = "GET ALL TRANSACTIONS OF A CUSTOMER",
            description = "Provides all the transaction details for a customer with given ID."
            )
    @ApiResponse(
            responseCode = "200",
            description = "Transactions shown successfully"
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CUSTOMER')")
    @GetMapping("{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccountId(@PathVariable Long id){
        return new ResponseEntity<>(accountService.getAllTransactions(id),HttpStatus.OK);
    }


}


