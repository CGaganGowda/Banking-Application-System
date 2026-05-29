package com.Bank.app.integration;

import com.Bank.app.model.Account;
import com.Bank.app.repo.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @DisplayName("Get Account By ID")
    @Test
    @WithMockUser
    void getAccountById_existingAccount_returns200WithDto() throws Exception {

        Account account = Account.builder()
                .name("Integrate-Test")
                .balance(40000L)
                .build();

        accountRepository.save(account);

        mockMvc.perform(get("/api/accounts/{id}",account.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integrate-Test"))
                .andExpect(jsonPath("$.balance").value(40000L));
    }

    @Order(2)
    @DisplayName("Get Account by ID - Exception")
    @Test
    @WithMockUser
    void getAccountById_nonExistingAccount_returns404() throws Exception {


        mockMvc.perform(get("/api/accounts/99"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Order(3)
    @DisplayName("Account Creation")
    @Test
    @WithMockUser
    void createAccount_validBody_returns201() throws Exception {

        Account account = Account.builder()
                .name("Integrate-Test-1")
                .balance(5000)
                .build();

        mockMvc.perform(post("/api/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(account.getName()));
    }

    @Order(4)
    @DisplayName("Deposit Amount")
    @Test
    @WithMockUser
    void deposit_validAmount_returns200WithUpdatedBalance() throws Exception {
        Account account = Account.builder()
                .name("Integrate-Test-2")
                .balance(5000)
                .build();

        accountRepository.save(account);

        mockMvc.perform(put("/api/accounts/{id}/deposit", account.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 555.0}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(5555.0));
    }

}
