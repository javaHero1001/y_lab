package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email, String password, boolean isAdmin) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || !email.contains("@") || password == null || password.trim().isEmpty()) {
            return null; // Простая валидация
        }
        if (findUserByEmail(email) != null) {
            return null; // Пользователь с таким email уже существует
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAdmin(isAdmin);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        User user = findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId);
    }

    public boolean updateUser(Long userId, String name, String email, String password) {
        User user = userRepository.findById(userId);
        if (user == null) return false;

        boolean updated = false;
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
            updated = true;
        }
        if (email != null && !email.trim().isEmpty() && email.contains("@")) {
            if (findUserByEmail(email) != null && !findUserByEmail(email).getId().equals(userId)) {
                return false; // Email занят другим пользователем
            }
            user.setEmail(email);
            updated = true;
        }
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password);
            updated = true;
        }
        if (updated) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean deleteUser(Long userId) {
        if (userId == null || userRepository.findById(userId) == null) {
            return false;
        }
        userRepository.delete(userId);
        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean blockUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.setBlocked(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean unblockUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.setBlocked(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Новый метод для поиска пользователя по email
    public User findUserByEmail(String email) {
        return userRepository.findByMail(email);
    }

    public void createFirstAdminIfNotExists(String email, String password) {
        // Проверяем, существует ли уже пользователь с указанным email
        if (findUserByEmail(email) == null) {
            // Если нет, создаем нового администратора
            User firstAdmin = new User();
            firstAdmin.setName("Администратор");
            firstAdmin.setEmail(email);
            firstAdmin.setPassword(password);
            firstAdmin.setAdmin(true); // Ставим флаг администратора
            firstAdmin.setBlocked(false);

            // Сохраняем нового администратора
            userRepository.save(firstAdmin);
            System.out.println("Первый администратор создан успешно.");
        } else {
            System.out.println("Администратор с указанным email уже существует.");
        }
    }
}