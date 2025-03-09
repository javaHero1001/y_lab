package org.example.service;

import org.example.model.Goal;
import org.example.repository.GoalRepository;

import java.time.LocalDate;
import java.util.List;

public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal createGoal(Long userId, String name, double targetAmount, LocalDate deadline) {
        if (userId == null || name == null || name.trim().isEmpty() || targetAmount <= 0 || deadline == null || deadline.isBefore(LocalDate.now())) {
            return null; // Простая валидация
        }
        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setName(name);
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(0);
        goal.setDeadline(deadline);
        return goalRepository.save(goal);
    }

    public Goal getGoalById(Long goalId) {
        if (goalId == null) return null;
        return goalRepository.findById(goalId);
    }

    public List<Goal> getGoalsByUserId(Long userId) {
        if (userId == null) return null;
        return goalRepository.findByUserId(userId);
    }

    public boolean updateGoal(Long goalId, String name, Double targetAmount, LocalDate deadline, Double currentAmount) {
        Goal goal = goalRepository.findById(goalId);
        if (goal == null) return false;

        boolean updated = false;
        if (name != null && !name.trim().isEmpty()) {
            goal.setName(name);
            updated = true;
        }
        if (targetAmount != null && targetAmount > 0) {
            goal.setTargetAmount(targetAmount);
            updated = true;
        }
        if (deadline != null && !deadline.isBefore(LocalDate.now())) {
            goal.setDeadline(deadline);
            updated = true;
        }
        if (currentAmount != null && currentAmount >= 0) {
            goal.setCurrentAmount(currentAmount);
            updated = true;
        }
        if (updated) {
            goalRepository.save(goal);
            return true;
        }
        return false;
    }

    public boolean deleteGoal(Long goalId) {
        if (goalId == null || goalRepository.findById(goalId) == null) {
            return false;
        }
        goalRepository.delete(goalId);
        return true;
    }

    public double calculateProgress(Long goalId) {
        Goal goal = goalRepository.findById(goalId);
        if (goal != null && goal.getTargetAmount() != 0) {
            return (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
        }
        return 0;
    }

    // Простой метод для обновления прогресса
    public boolean updateGoalProgress(Long goalId, double amountToAdd) {
        if (goalId == null || amountToAdd < 0) return false;
        Goal goal = goalRepository.findById(goalId);
        if (goal == null) return false;
        goal.setCurrentAmount(goal.getCurrentAmount() + amountToAdd);
        goalRepository.save(goal);
        return true;
    }
}