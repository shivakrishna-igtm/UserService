package com.example.userservice.Controller;

import com.example.userservice.DTO.*;
import com.example.userservice.Exceptions.InvalidCredentials;
import com.example.userservice.Exceptions.UserNotFoundException;
import com.example.userservice.Models.SessionStatus;
import com.example.userservice.Service.AuthService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    @Autowired
    public AuthController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody UserSignUpDto userSignUpDto){
        UserDto userDto= authService.signUp(userSignUpDto.getEmail(), userSignUpDto.getPassword());
         return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginReqDto request) throws UserNotFoundException, InvalidCredentials {
        return authService.login(request.getEmail(), request.getPassword());
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutReqDto reqDto){
        return authService.logout(reqDto.getToken(),reqDto.getUserId());
    }
    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(ValidateTokenRequestDto request) {
        SessionStatus sessionStatus = authService.validate(request.getToken(), request.getUserId());

        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }
}
