package tn.esprit.spring.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // autoriser pré-vol
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/publications/add").hasAuthority("Presse")
                .requestMatchers("/publications/mine").permitAll()
                .requestMatchers("/publications/getPublication/**").hasAuthority("Presse")
                .requestMatchers("/publications/publications/**").hasAuthority("Spectateur")
                .requestMatchers("/start-live/**").hasAuthority("Presse")
                .requestMatchers("/commentaires/**").permitAll()
                .requestMatchers("/commentaires/add").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .anyRequest().authenticated();
        // Require authentication for all other requests
        return http.build();
    }


}
