package com.youneskarir.springsecuritydemo.advice;


import com.youneskarir.springsecuritydemo.advice.custom.NotValidTokenException;
import com.youneskarir.springsecuritydemo.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class ExceptionHandle {
    
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidation(MethodArgumentNotValidException exception){
        Map<String,String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                item -> errors.put(item.getField(), item.getDefaultMessage())
        );
        return errors;
    }
    
    
//    @ExceptionHandler(NotValidTokenException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ResponseEntity<Object> handleNotValidTokenException(NotValidTokenException exception){
//        return ResponseHandler.build(
//                exception.getMessage(),
//                HttpStatus.FORBIDDEN,
//                null
//        );
//    }
}
