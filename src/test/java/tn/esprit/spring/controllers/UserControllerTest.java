package tn.esprit.spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId((long) 1);
        user1.setNom("John");
        user1.setPrenom("Doe");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId((long) 2);
        user2.setNom("Jane");
        user2.setPrenom("Smith");
        user2.setEmail("jane@example.com");

        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById_found() {
        User user = new User();
        user.setId((long) 1);
        user.setNom("John");
        user.setPrenom("Doe");
        user.setEmail("john@example.com");

        when(userService.getUserById(1)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserById_notFound() {
        when(userService.getUserById(1)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testCreateUser() {
        User userToCreate = new User();
        userToCreate.setNom("John");
        userToCreate.setPrenom("Doe");
        userToCreate.setEmail("john@example.com");

        User createdUser = new User();
        createdUser.setId((long) 1);
        createdUser.setNom("John");
        createdUser.setPrenom("Doe");
        createdUser.setEmail("john@example.com");

        when(userService.createUser(userToCreate)).thenReturn(createdUser);

        User result = userController.createUser(userToCreate);

        assertEquals(createdUser, result);
        verify(userService, times(1)).createUser(userToCreate);
    }

    @Test
    void testUpdateUser_success() {
        User userDetails = new User();
        userDetails.setNom("John Updated");
        userDetails.setPrenom("Doe Updated");
        userDetails.setEmail("john_updated@example.com");

        User updatedUser = new User();
        updatedUser.setId((long) 1);
        updatedUser.setNom("John Updated");
        updatedUser.setPrenom("Doe Updated");
        updatedUser.setEmail("john_updated@example.com");

        when(userService.updateUser(1, userDetails)).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(1, userDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    void testUpdateUser_notFound() {
        User userDetails = new User();
        userDetails.setNom("John Updated");
        userDetails.setPrenom("Doe Updated");
        userDetails.setEmail("john_updated@example.com");

        when(userService.updateUser(1, userDetails)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<User> response = userController.updateUser(1, userDetails);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(1);
    }
}
