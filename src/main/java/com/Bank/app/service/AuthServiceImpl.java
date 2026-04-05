package com.Bank.app.service;

import com.Bank.app.dto.LoginDto;
import com.Bank.app.dto.RoleDto;
import com.Bank.app.dto.UserDto;
import com.Bank.app.exception.TodoApiException;
import com.Bank.app.model.Role;
import com.Bank.app.model.User;
import com.Bank.app.repo.RoleRepo;
import com.Bank.app.repo.UserRepo;
import com.Bank.app.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    private JwtTokenProvider jwtTokenProvider;

    @Override
    public User register(UserDto userDto) {


        if(userRepo.existsByUsername(userDto.getUsername())){
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if(userRepo.existsByEmail(userDto.getEmail())){
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepo.findByName("ROLE_USER"));

        user.setRoles(roles);

        User savedUser = userRepo.save(user);
        return savedUser;
    }

    /*
    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(),loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }
     */

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public Role addRole(RoleDto roleDto) {
        Role role = new Role();
        role.setName(roleDto.getName());
        Role saved = roleRepo.save(role);
        return saved;
    }


}
