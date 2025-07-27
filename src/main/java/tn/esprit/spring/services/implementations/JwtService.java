package tn.esprit.spring.services.implementations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.spring.entities.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 86400000;  // 1 jour
    private static final String SECRET_KEY = "mySecretKeyForJWTTokenGenerationAndValidation123456789012345678901234567890";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static String generateToken(User user) {
        System.out.println("JwtService - Generating token with HS384");
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS384)  // Use HS384 for new tokens
                .compact();
    }
    public String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication object: " + authentication);

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Extract details (the role) from the authentication
            Object details = authentication.getDetails();
            if (details instanceof String) {
                // If details are a string (the role), return it
                return (String) details;
            }
        }

        // Return null if the user is not authenticated or the role is not found
        return null;
    }
    public String getEmailFromAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    private Claims getClaimsFromToken(String token) {
        // Try HS384 first, then HS256
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e1) {
            try {
                Key hs256Key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                return Jwts.parserBuilder()
                        .setSigningKey(hs256Key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (Exception e2) {
                throw e2;
            }
        }
    }

    public boolean isValidToken(String token) {
        try {
            System.out.println("JwtService - Starting token validation...");
            System.out.println("JwtService - Token length: " + token.length());
            System.out.println("JwtService - Token starts with: " + token.substring(0, Math.min(20, token.length())));
            
            // Try HS384 first (for new tokens)
            try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);
                System.out.println("JwtService - Token validation SUCCESS with HS384");
                return true;
            } catch (Exception e1) {
                System.out.println("JwtService - HS384 validation failed, trying HS256...");
                
                // Fallback to HS256 (for old tokens)
                try {
                    Key hs256Key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                    Jwts.parserBuilder()
                            .setSigningKey(hs256Key)
                            .build()
                            .parseClaimsJws(token);
                    System.out.println("JwtService - Token validation SUCCESS with HS256");
                    return true;
                } catch (Exception e2) {
                    System.out.println("JwtService - Both HS384 and HS256 validation failed");
                    throw e2;
                }
            }
        } catch (Exception e) {
            System.out.println("JwtService - Token validation FAILED: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }
}
