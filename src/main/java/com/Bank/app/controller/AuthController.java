package com.Bank.app.controller;

// import com.Management.todo.dto.JwtAuthResponse;
// import com.Management.todo.dto.LoginDto;
// import com.Management.todo.dto.UserDto;
// import com.Management.todo.model.User;
// import com.Management.todo.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    public AuthService authService;

    @PostMapping("register")
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDto){
        User user = authService.register(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<JwtAuthResponse> loginUser(@RequestBody LoginDto  loginDto){
       String token =  authService.login(loginDto);

       JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
       jwtAuthResponse.setAccessToken(token);
       return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }
}

