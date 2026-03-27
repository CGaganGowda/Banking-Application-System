package com.Management.todo.service;

import com.Management.todo.dto.LoginDto;
import com.Management.todo.dto.RoleDto;
import com.Management.todo.dto.UserDto;
import com.Management.todo.model.Role;
import com.Management.todo.model.User;

import java.util.Optional;

public interface AuthService {
    User register(UserDto userDto);
    String login(LoginDto loginDto);
    Role addRole(RoleDto roleDto);
}
