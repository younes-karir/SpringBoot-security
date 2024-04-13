package com.youneskarir.springsecuritydemo.config;


import com.youneskarir.springsecuritydemo.dto.AuthenticationRequest;
import com.youneskarir.springsecuritydemo.dto.AuthenticationResponse;
import com.youneskarir.springsecuritydemo.dto.RegisterRequest;
import com.youneskarir.springsecuritydemo.model.User;
import com.youneskarir.springsecuritydemo.repository.UserRepository;
import com.youneskarir.springsecuritydemo.token.Token;
import com.youneskarir.springsecuritydemo.token.TokenRepository;
import com.youneskarir.springsecuritydemo.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        persistToken(savedUser,jwtToken);
        return  AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
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
        persistToken(user,jwtToken);
        return  AuthenticationResponse.builder() 
                .token(jwtToken)
                .role(user.getRole())
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
}
