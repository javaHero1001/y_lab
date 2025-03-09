
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldRegisterUser_WhenValidParametersProvided() {
        // Arrange
        String name = "TestUser";
        String email = "test@example.com";
        String password = "password";
        boolean isAdmin = false;

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setName(name);
        expectedUser.setEmail(email);
        expectedUser.setPassword(password);
        expectedUser.setAdmin(isAdmin);
        expectedUser.setBlocked(false);

        when(userRepository.findByMail(email)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User actualUser = userService.registerUser(name, email, password, isAdmin);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldReturnNull_WhenEmailAlreadyExists() {
        // Arrange
        String name = "TestUser";
        String email = "existing@example.com";
        String password = "password";
        boolean isAdmin = false;

        when(userRepository.findByMail(email)).thenReturn(new User());

        // Act
        User actualUser = userService.registerUser(name, email, password, isAdmin);

        // Assert
        assertNull(actualUser);
    }

    @Test
    void loginUser_ShouldReturnUser_WhenValidCredentialsProvided() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setPassword(password);

        when(userRepository.findByMail(email)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.loginUser(email, password);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void loginUser_ShouldReturnNull_WhenInvalidCredentialsProvided() {
        // Arrange
        String email = "invalid@example.com";
        String password = "password";

        when(userRepository.findByMail(email)).thenReturn(null);

        // Act
        User actualUser = userService.loginUser(email, password);

        // Assert
        assertNull(actualUser);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.getUserById(userId);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_ShouldReturnNull_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(null);

        // Act
        User actualUser = userService.getUserById(userId);

        // Assert
        assertNull(actualUser);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenValidParametersProvided() {
        // Arrange
        Long userId = 1L;
        String newName = "UpdatedName";
        String newEmail = "updated@example.com";
        String newPassword = "newpassword";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("OldName");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpassword");

        when(userRepository.findById(userId)).thenReturn(existingUser);
        when(userRepository.findByMail(newEmail)).thenReturn(null);

        // Act
        boolean result = userService.updateUser(userId, newName, newEmail, newPassword);

        // Assert
        assertTrue(result);
        assertEquals(newName, existingUser.getName());
        assertEquals(newEmail, existingUser.getEmail());
        assertEquals(newPassword, existingUser.getPassword());
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    void updateUser_ShouldNotUpdateUser_WhenEmailAlreadyExists() {
        // Arrange
        Long userId = 1L;
        String newName = "UpdatedName";
        String newEmail = "existing@example.com";
        String newPassword = "newpassword";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("OldName");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpassword");

        when(userRepository.findById(userId)).thenReturn(existingUser);
        when(userRepository.findByMail(newEmail)).thenReturn(new User());

        // Act
        boolean result = userService.updateUser(userId, newName, newEmail, newPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertTrue(result);
        Mockito.verify(userRepository, Mockito.times(1)).delete(userId);
    }

    @Test
    void deleteUser_ShouldNotDeleteUser_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(null);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers_WhenUsersExist() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertTrue(actualUsers.contains(user1));
        assertTrue(actualUsers.contains(user2));
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenUsersDoNotExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
    }

    @Test
    void blockUser_ShouldBlockUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setBlocked(false);

        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Act
        boolean result = userService.blockUser(userId);

        // Assert
        assertTrue(result);
        assertTrue(existingUser.isBlocked());
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    void blockUser_ShouldNotBlockUser_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(null);

        // Act
        boolean result = userService.blockUser(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void unblockUser_ShouldUnblockUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setBlocked(true);

        when(userRepository.findById(userId)).thenReturn(existingUser);

        // Act
        boolean result = userService.unblockUser(userId);

        // Assert
        assertTrue(result);
        assertFalse(existingUser.isBlocked());
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    void unblockUser_ShouldNotUnblockUser_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(null);

        // Act
        boolean result = userService.unblockUser(userId);

        // Assert
        assertFalse(result);
    }
}