package tn.esprit.spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import tn.esprit.spring.entities.AuthResponse;
import tn.esprit.spring.entities.LoginRequest;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.entities.UserDTO;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.UserService;
import tn.esprit.spring.services.implementations.EmailService;
import tn.esprit.spring.services.implementations.PasswordResetService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_success() {
        UserDTO userDTO = new UserDTO();
        userDTO.email = "test@example.com";
        // configure other userDTO fields if needed

        AuthResponse mockResponse = new AuthResponse("mock-token");
        User mockUser = new User();
        mockUser.setEmail("test@example.com");

        when(userService.register(userDTO)).thenReturn(mockResponse);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        try {
            doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        } catch (Exception e) {
            // Won't be reached in mocking
        }

        ResponseEntity<AuthResponse> response = authController.register(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).register(userDTO);
    }

    @Test
    void testLogin_success() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");

        AuthResponse mockResponse = new AuthResponse("mock-token");

        when(userService.login(loginRequest)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(userService, times(1)).login(loginRequest);
    }

    @Test
    void testForgotPassword_emailNotFound() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.forgotPassword(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Email introuvable", response.getBody());
    }
    @Test
    void testForgotPassword_success() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "user@example.com");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("oldPassword");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        try {
            when(emailService.sendTemporaryPassword("user@example.com")).thenReturn("TempPass123");
        } catch (Exception e) {
            // This won't be reached in mocking, but satisfies compiler
        }

        ResponseEntity<?> response = authController.forgotPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertNotNull(body);
        assertEquals("TempPass123", body.get("tempPassword"));

        verify(userRepository).save(any(User.class));
        try {
            verify(emailService).sendTemporaryPassword("user@example.com");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    void testResetPassword_emailNotFound() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "unknown@example.com");
        request.put("tempPassword", "temp");
        request.put("newPassword", "newPass");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.resetPassword(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Email introuvable", body.get("message"));
    }

    @Test
    void testResetPassword_tempPasswordInvalid() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("otherTemp"));

        Map<String, String> request = new HashMap<>();
        request.put("email", "user@example.com");
        request.put("tempPassword", "wrongTemp");
        request.put("newPassword", "newPass");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.resetPassword(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Mot de passe temporaire invalide", body.get("message"));
    }

    @Test
    void testResetPassword_success() {
        String tempPass = "TempPass123";
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(encoder.encode(tempPass));

        Map<String, String> request = new HashMap<>();
        request.put("email", "user@example.com");
        request.put("tempPassword", tempPass);
        request.put("newPassword", "newPass");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Mot de passe mis à jour avec succès", body.get("message"));

        verify(userRepository).save(user);
    }
}
