package com.youneskarir.springsecuritydemo.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    
    @NotBlank(message = "email is empty")
    @Email(message = "not valid email format")
    private String email;
    
    @NotBlank(message = "password is empty")
    @Size(min = 8,message = "password less than 8 characters")
    private String password;
}
