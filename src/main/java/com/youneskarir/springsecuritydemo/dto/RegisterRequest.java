package com.youneskarir.springsecuritydemo.dto;

import com.youneskarir.springsecuritydemo.model.Role;
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
public class RegisterRequest {
    
    @NotBlank(message = "first name is empty")
    @Size(min = 3,message = "first name less than 3 characters")
    private String firstName;
    
    @NotBlank(message = "first name is empty")
    @Size(min = 3,message = "last name less than 3 characters")
    private String lastName;
    
    @NotBlank(message = "email is empty")
    @Email(message = "not valid email format")
    private String email;

    @NotBlank(message = "password is empty")
    @Size(min = 8,message = "password less than 8 characters")
    private String password;
    
    private Role role;
}
