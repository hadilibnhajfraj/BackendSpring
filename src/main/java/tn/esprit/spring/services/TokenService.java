package tn.esprit.spring.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {
    // Generate a simple random UUID token
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
