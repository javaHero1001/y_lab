package org.example.service;

import org.example.model.Transaction;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_ShouldReturnAllUsers_WhenUsersExist() {
        // Arrange
        User user1 = new User(1L, "User1", "user1@example.com", "password", false, false);
        User user2 = new User(2L, "User2", "user2@example.com", "password", false, false);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> users = adminService.getUsers();

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void getUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<User> users = adminService.getUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void getUserTransactions_ShouldReturnAllTransactions_WhenUserHasTransactions() {
        // Arrange
        User user = new User(1L, "User1", "user1@example.com", "password", false, false);
        when(userRepository.findById(1L)).thenReturn(user);

        // Act
        List<Transaction> transactions = adminService.getUserTransactions(1L);

        // Assert
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void blockUser_ShouldBlockUser_WhenUserExists() {
        // Arrange
        User user = new User(1L, "User1", "user1@example.com", "password", false, false);
        when(userRepository.findById(1L)).thenReturn(user);

        // Act
        boolean result = adminService.blockUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void blockUser_ShouldNotBlockUser_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(null);

        // Act
        boolean result = adminService.blockUser(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        User user = new User(1L, "User1", "user1@example.com", "password", false, false);
        when(userRepository.findById(1L)).thenReturn(user);

        // Act
        boolean result = adminService.deleteUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).delete(1L);
    }

    @Test
    void deleteUser_ShouldNotDeleteUser_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(null);

        // Act
        boolean result = adminService.deleteUser(1L);

        // Assert
        assertFalse(result);
    }
}