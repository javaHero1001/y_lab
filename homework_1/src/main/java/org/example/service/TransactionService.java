package org.example.service;

import org.example.model.Transaction;
import org.example.model.TransactionType;
import org.example.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Long userId, double amount, String category, String description, LocalDateTime date, TransactionType type) {
        if (userId == null || category == null || category.trim().isEmpty() || date == null || amount == 0 || type == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setDate(date);
        transaction.setType(type);
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(Long transactionId) {
        if (transactionId == null) {
            return null;
        }
        return transactionRepository.findById(transactionId);
    }

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (userId == null || startDate == null || endDate == null) {
            return null;
        }
        return transactionRepository.findByUserId(userId).stream()
                .filter(transaction -> !transaction.getDate().isBefore(startDate) &&
                        !transaction.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public boolean updateTransaction(Long transactionId, Double amount, String category, String description) {
        Transaction transaction = transactionRepository.findById(transactionId);
        if (transaction == null) {
            return false;
        }
        boolean updated = false;
        if (amount != null) {
            transaction.setAmount(amount);
            updated = true;
        }
        if (category != null && !category.trim().isEmpty()) {
            transaction.setCategory(category);
            updated = true;
        }
        if (description != null) {
            transaction.setDescription(description);
            updated = true;
        }
        if (updated) {
            transactionRepository.save(transaction);
            return true;
        }
        return false;
    }

    public boolean deleteTransaction(Long transactionId) {
        if (transactionId == null || transactionRepository.findById(transactionId) == null) {
            return false;
        }
        transactionRepository.delete(transactionId);
        return true;
    }

    public double calculateTotalIncome(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = getTransactionsByUserIdAndDateRange(userId, startDate, endDate);
        if (transactions == null) {
            return 0;
        }
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalExpenses(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = getTransactionsByUserIdAndDateRange(userId, startDate, endDate);
        if (transactions == null) {
            return 0;
        }
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateBalance(Long userId) {
        List<Transaction> transactions = getAllTransactionsByUserId(userId);
        if (transactions == null) {
            return 0;
        }
        double income = transactions.stream().filter(t -> t.getType() == TransactionType.INCOME).mapToDouble(Transaction::getAmount).sum();
        double expenses = transactions.stream().filter(t -> t.getType() == TransactionType.EXPENSE).mapToDouble(Transaction::getAmount).sum();
        return income - expenses;
    }

    public Map<String, Double> calculateExpensesByCategory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = getTransactionsByUserIdAndDateRange(userId, startDate, endDate);
        if (transactions == null) {
            return Map.of();
        }
        return transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE) // Только расходы
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
    }
}