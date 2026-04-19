package com.Bank.app.controller;

import com.Bank.app.dto.AccountDto;
import com.Bank.app.exception.AccountNotFoundException;
import com.Bank.app.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAccountById_existingAccount_returns200WithDto() throws Exception {
        AccountDto dto = new AccountDto(1L, "Gagan Gowda", 5000.0);
        when(accountService.getAccountById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountHolderName").value("Gagan Gowda"))
                .andExpect(jsonPath("$.balance").value(5000.0));
    }

    @Test
    @WithMockUser
    void getAccountById_nonExistingAccount_returns404() throws Exception {
        when(accountService.getAccountById(99L))
                .thenThrow(new AccountNotFoundException("Account not found with id: 99"));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createAccount_validBody_returns201() throws Exception {
        AccountDto request = new AccountDto(null, "Gagan Gowda", 1000.0);
        AccountDto saved   = new AccountDto(1L,   "Gagan Gowda", 1000.0);
        when(accountService.createAccount(any())).thenReturn(saved);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void deposit_validAmount_returns200WithUpdatedBalance() throws Exception {
        AccountDto updated = new AccountDto(1L, "Gagan Gowda", 1500.0);
        when(accountService.deposit(1L, 500.0)).thenReturn(updated);

        mockMvc.perform(put("/api/accounts/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 500.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0));
    }
}
