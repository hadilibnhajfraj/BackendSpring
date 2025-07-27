package tn.esprit.spring.services.implementations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        String path = request.getRequestURI();

        System.out.println("JWT Filter - Path: " + path);
        System.out.println("JWT Filter - Token: " + (token != null ? "Present" : "Missing"));
        
        if (token != null) {
            System.out.println("JWT Filter - Token first 50 chars: " + token.substring(0, Math.min(50, token.length())));
            boolean isValid = jwtService.isValidToken(token);
            System.out.println("JWT Filter - Token validation result: " + isValid);
            
            if (isValid) {
                String email = jwtService.getEmailFromToken(token);
                String role = jwtService.getRoleFromToken(token);
                System.out.println("JWT Filter - Valid token. Email: " + email + ", Role: " + role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority(role)));
                authentication.setDetails(role);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("JWT Filter - Authentication set successfully");
            } else {
                System.out.println("JWT Filter - Token validation failed for path: " + path);
            }
        } else {
            System.out.println("JWT Filter - No token found for path: " + path);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Supprimer le préfixe "Bearer "
        }
        return null;
    }
}
