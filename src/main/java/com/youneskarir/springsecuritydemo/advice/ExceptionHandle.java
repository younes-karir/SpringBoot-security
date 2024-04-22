package com.youneskarir.springsecuritydemo.advice;


import com.youneskarir.springsecuritydemo.advice.custom.ElementExistException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandle {
    
    
    
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ElementExistException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<Object> handleValidation(Exception exception){
        Map<String,String> errors = new HashMap<>();
        
        if(exception instanceof MethodArgumentNotValidException data){
            data.getBindingResult().getFieldErrors().forEach(
                    item -> errors.put(item.getField(), item.getDefaultMessage())
            );
        }
        if(exception instanceof ElementExistException data){
            errors.put("conflict", data.getMessage());
        }

        if(exception instanceof UsernameNotFoundException data){
            errors.put("email", data.getMessage());
        }
        
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }
    
    
    @ExceptionHandler({
            BadCredentialsException.class,
            AccountStatusException.class,
            AccessDeniedException.class,
            SignatureException.class,
            ExpiredJwtException.class
    })
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        // TODO send this stack trace to an observability tool
        //exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");
        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired");
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
        }

        return errorDetail;
    }


    }
