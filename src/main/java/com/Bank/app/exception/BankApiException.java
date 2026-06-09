package com.Bank.app.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class BankApiException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
