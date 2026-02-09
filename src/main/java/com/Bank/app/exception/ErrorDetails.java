package com.Bank.app.exception;

import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime time, String message, String details, String errorCode) {
}
