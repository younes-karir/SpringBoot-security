package com.youneskarir.springsecuritydemo.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {
    
    @GetMapping
    public String getDemo(){
        return "<h1>spring security test demo</h1>";
    }
    
    /*
    @GetMapping("one")
    //@PreAuthorize("hasRole('USER')")
    public String getDemoOne(){
        return "<h1>you are a user</h1>";
    }
    
    @GetMapping("two")
    //@PreAuthorize("hasRole({'ADMIN','USER'})")
     public String getDemoTwo(){
        return "<h1>you are an admin</h1>";
    }
    
    @GetMapping("three")
    public String getDemoThree(){
        return "<h1>spring security test demo THREE</h1>";
    }
    */
}
