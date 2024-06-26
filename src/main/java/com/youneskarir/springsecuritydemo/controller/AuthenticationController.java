package com.youneskarir.springsecuritydemo.controller;


import com.youneskarir.springsecuritydemo.config.AuthenticationService;
import com.youneskarir.springsecuritydemo.dto.AuthenticationRequest;
import com.youneskarir.springsecuritydemo.dto.AuthenticationResponse;
import com.youneskarir.springsecuritydemo.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request, 
            HttpServletResponse response
            ) throws IOException {
        authenticationService.refreshToken(request,response);
    }
}
