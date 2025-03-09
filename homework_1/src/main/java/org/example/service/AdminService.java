package org.example.service;

import org.example.repository.UserRepository;
import org.example.repository.TransactionRepository;
import org.example.model.Transaction;
import org.example.model.User;

import java.util.List;

public class AdminService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
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

    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}