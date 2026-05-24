package com.Bank.app.controller;

import jakarta.servlet.FilterChain;
import com.Bank.app.config.SpringSecurityConfig;
import com.Bank.app.dto.AccountDto;
import com.Bank.app.exception.GlobalExceptionHandler;
import com.Bank.app.exception.IdNotFoundException;
import com.Bank.app.security.CustomUserDetailsService;
import com.Bank.app.security.JwtAuthenticationEntryPoint;
import com.Bank.app.security.JwtAuthenticationFilter;
import com.Bank.app.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WebMvcTest(AccountController.class)
@Import({SpringSecurityConfig.class, GlobalExceptionHandler.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;


    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {

        doAnswer(invocation -> {

            var request = invocation.getArgument(0);
            var response = invocation.getArgument(1);
            var chain = invocation.getArgument(2, FilterChain.class);

            chain.doFilter((ServletRequest) request, (ServletResponse) response);

            return null;

        }).when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());
    }

    @Order(1)
    @Test
    @WithMockUser
    void getAccountById_existingAccount_returns200WithDto() throws Exception {
        AccountDto dto = new AccountDto(1L, "Gagan Gowda", 5000.0);
        when(accountService.getAccountById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gagan Gowda"))
                .andExpect(jsonPath("$.balance").value(5000.0));
    }

    @Order(2)
    @Test
    @WithMockUser
    void getAccountById_nonExistingAccount_returns404() throws Exception {
        when(accountService.getAccountById(99L))
                .thenThrow(new IdNotFoundException("Account not found with id: 99"));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound());
    }

    @Order(3)
    @Test
    @WithMockUser
    void createAccount_validBody_returns201() throws Exception {
        AccountDto request = new AccountDto(null, "Gagan Gowda", 1000.0);
        AccountDto saved   = new AccountDto(1L,   "Gagan Gowda", 1000.0);
        when(accountService.createAccount(any())).thenReturn(saved);

        mockMvc.perform(post("/api/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Order(4)
    @Test
    @WithMockUser
    void deposit_validAmount_returns200WithUpdatedBalance() throws Exception {
        AccountDto updated = new AccountDto(1L, "Gagan Gowda", 1500.0);
        when(accountService.deposit(1L, 500.0)).thenReturn(updated);

        mockMvc.perform(put("/api/accounts/1/deposit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 500.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0));
    }
}