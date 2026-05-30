package com.Bank.app.service;

import com.Bank.app.dto.LoginDto;
import com.Bank.app.dto.RegisterDto;
import  com.Bank.app.dto.RoleDto;
import  com.Bank.app.dto.UserDto;
import  com.Bank.app.model.Role;
import com.Bank.app.model.User;


public interface AuthService {
    RegisterDto register(UserDto userDto);
    String login(LoginDto loginDto);
    Role addRole(RoleDto roleDto);
}
