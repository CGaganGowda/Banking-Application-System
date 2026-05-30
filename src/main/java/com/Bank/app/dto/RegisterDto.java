package com.Bank.app.dto;

import com.Bank.app.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String username;
    private String password;
    private String email;
    private String name;
    private Set<Role> roles;
}
