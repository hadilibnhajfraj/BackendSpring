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

        // Log the received token
        System.out.println("Received token: " + token);

        if (token != null && jwtService.isValidToken(token)) {
            String email = jwtService.getEmailFromToken(token);
            String role = jwtService.getRoleFromToken(token);
            System.out.println("Token is valid. Extracted email: " + email + ", Role: " + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority(role)));

            // Ajout du rôle dans les détails
            authentication.setDetails(role); // Le rôle est ajouté dans les détails

            // Configurez l'authentification dans le contexte
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // Log if the token is invalid or missing
            System.out.println("Invalid or missing token.");
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