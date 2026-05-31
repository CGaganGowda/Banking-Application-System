package com.Bank.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;

    @NotEmpty(message = "Account name should not be null.")
    @Size(min = 4, max = 100)
    private String name;

    @Positive
    @Min(0)
    private BigDecimal balance;
}
