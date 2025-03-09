package org.example.repository;

import org.example.model.Goal;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class GoalRepository {
    private Map<Long, Goal> goals = new HashMap<>();
    private Long goalId = 0L;

    public Goal save(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }
        if (goal.getId() == null) {
            goal.setId(++goalId);
        }
        goals.put(goal.getId(), goal);
        return goal;
    }

    public Goal findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return goals.get(id);
    }

    public List<Goal> findAll() {
        return new ArrayList<>(goals.values());
    }

    public List<Goal> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return goals.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        goals.remove(id);
    }
}