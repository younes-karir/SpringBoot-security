package com.youneskarir.springsecuritydemo.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.youneskarir.springsecuritydemo.advice.custom.ElementExistException;
import com.youneskarir.springsecuritydemo.dto.AuthenticationRequest;
import com.youneskarir.springsecuritydemo.dto.AuthenticationResponse;
import com.youneskarir.springsecuritydemo.dto.RegisterRequest;
import com.youneskarir.springsecuritydemo.model.User;
import com.youneskarir.springsecuritydemo.repository.UserRepository;
import com.youneskarir.springsecuritydemo.token.Token;
import com.youneskarir.springsecuritydemo.token.TokenRepository;
import com.youneskarir.springsecuritydemo.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    
    public AuthenticationResponse register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) 
            throw new ElementExistException("email already used");
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        persistToken(savedUser,jwtToken);
        return  AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )               
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeUserTokens(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        persistToken(user,jwtToken);
        return  AuthenticationResponse.builder() 
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    private void persistToken(User user,String jwtToken){
        
        var token = Token.builder()
                .token(jwtToken)
                .user(user)  /// the reference of the owner
                .expired(false)
                .revoked(false)
                .tokenType(TokenType.BEARER)
                .build();
        tokenRepository.save(token);
    }
    
    private void revokeUserTokens(User user){
        var validUserTokens=tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(item -> {
            item.setRevoked(true);
            item.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String email;
        if(authHeader == null || !authHeader.startsWith("Bearer "))  return;
        refreshToken = authHeader.substring(7);
        email = jwtService.extractUsername(refreshToken);
        if(email != null) {
            var user = this.userRepository.findByEmail(email).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeUserTokens(user);
                persistToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
