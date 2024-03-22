/*package com.youneskarir.springsecuritydemo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityAuthentication {
        
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(
                (req) -> req.requestMatchers("demo","demo/one").permitAll()
                        .anyRequest().authenticated()
        ).formLogin(Customizer.withDefaults());
        
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsManager(){
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails user_1 = users
                .username("youneskarir")
                .password("123")
                .roles("USER")
                .build();
        UserDetails user_2 = users
                .username("khalidfatihi")
                .password("456")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user_1,user_2);
    }
    
    
}
*/
