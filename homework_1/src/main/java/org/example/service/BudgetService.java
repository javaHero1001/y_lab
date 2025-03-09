package org.example.service;

import org.example.model.Budget;
import org.example.repository.BudgetRepository;

import java.time.YearMonth;

public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    public BudgetService(BudgetRepository budgetRepository, TransactionService transactionService) {
        this.budgetRepository = budgetRepository;
        this.transactionService = transactionService;
    }

    public Budget createBudget(Long userId, double amount, YearMonth period) {
        if (userId == null || period == null || amount <= 0) {
            return null;
        }
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setAmount(amount);
        budget.setPeriod(period);
        return budgetRepository.save(budget);
    }

    public Budget getBudgetById(Long budgetId) {
        if (budgetId == null) return null;
        return budgetRepository.findById(budgetId);
    }

    public Budget getBudgetByUserIdAndPeriod(Long userId, YearMonth period) {
        if (userId == null || period == null) return null;
        return budgetRepository.findByUserId(userId).stream()
                .filter(b -> b.getPeriod().equals(period))
                .findFirst()
                .orElse(null);
    }

    public boolean updateBudget(Long budgetId, Double amount) {
        Budget budget = budgetRepository.findById(budgetId);
        if (budget != null && amount != null && amount > 0) {
            budget.setAmount(amount);
            budgetRepository.save(budget);
            return true;
        }
        return false;
    }

    public boolean deleteBudget(Long budgetId) {
        if (budgetId == null || budgetRepository.findById(budgetId) == null) {
            return false;
        }
        budgetRepository.delete(budgetId);
        return true;
    }

    public boolean isBudgetExceeded(Long userId, YearMonth period, TransactionService transactionService) {
        Budget budget = getBudgetByUserIdAndPeriod(userId, period);
        if (budget == null) {
            return false; // Бюджет не установлен
        }
        double totalExpenses = transactionService.calculateTotalExpenses(userId, period.atDay(1).atStartOfDay(), period.atEndOfMonth().atTime(23, 59, 59));
        return totalExpenses > budget.getAmount();
    }
}