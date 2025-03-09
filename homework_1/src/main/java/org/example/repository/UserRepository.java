package org.example.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.model.User;


public class UserRepository {
    private Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if(user.getId() == null) {
            user.setId(++userId);
        }
        users.put(user.getId(), user);
        return user;
    }
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return users.get(id);
    }

    public User findByMail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return users.values().stream().
                filter(user -> user.getEmail().equals(email)).
                findFirst().
                orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean delete(Long id) {
        if (id == null || users.get(id) == null) {
            return false;
        }
        users.remove(id);
        return true;
    }
}
