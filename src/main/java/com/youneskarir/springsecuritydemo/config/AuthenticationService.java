package com.youneskarir.springsecuritydemo.config;


import com.youneskarir.springsecuritydemo.dto.AuthenticationRequest;
import com.youneskarir.springsecuritydemo.dto.AuthenticationResponse;
import com.youneskarir.springsecuritydemo.dto.RegisterRequest;
import com.youneskarir.springsecuritydemo.model.Role;
import com.youneskarir.springsecuritydemo.model.User;
import com.youneskarir.springsecuritydemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return  AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
    return null;
    }
}
