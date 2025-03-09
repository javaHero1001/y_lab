package org.example.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.example.model.Budget;

@Data
public class BudgetRepository {
    private Map<Long, Budget> budgets = new HashMap<>();
    private Long budgetId = 0L;

    public Budget save(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }
        if (budget.getId() == null) {
            budget.setId(++budgetId);
        }
        budgets.put(budget.getId(), budget);
        return budget;
    }

    public Budget findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return budgets.get(id);
    }

    public List<Budget> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return budgets.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Budget> findAll() {
        return new ArrayList<>(budgets.values());
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        budgets.remove(id);
    }
}
