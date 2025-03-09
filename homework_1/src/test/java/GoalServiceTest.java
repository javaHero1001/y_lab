

import org.example.model.Goal;
import org.example.repository.GoalRepository;
import org.example.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGoal_ShouldCreateGoal_WhenValidParametersProvided() {
        // Arrange
        Long userId = 1L;
        String name = "Vacation Savings";
        double targetAmount = 10000.0;
        LocalDate deadline = LocalDate.of(2025, 12, 31);

        Goal expectedGoal = new Goal();
        expectedGoal.setId(1L);
        expectedGoal.setUserId(userId);
        expectedGoal.setName(name);
        expectedGoal.setTargetAmount(targetAmount);
        expectedGoal.setCurrentAmount(0.0);
        expectedGoal.setDeadline(deadline);

        when(goalRepository.save(any(Goal.class))).thenReturn(expectedGoal);

        // Act
        Goal actualGoal = goalService.createGoal(userId, name, targetAmount, deadline);

        // Assert
        assertNotNull(actualGoal);
        assertEquals(expectedGoal, actualGoal);
        Mockito.verify(goalRepository, Mockito.times(1)).save(any(Goal.class));
    }

    @Test
    void createGoal_ShouldReturnNull_WhenInvalidParametersProvided() {
        // Arrange
        Long userId = null;
        String name = "Vacation Savings";
        double targetAmount = 10000.0;
        LocalDate deadline = LocalDate.of(2025, 12, 31);

        // Act
        Goal actualGoal = goalService.createGoal(userId, name, targetAmount, deadline);

        // Assert
        assertNull(actualGoal);
    }

    @Test
    void getGoalById_ShouldReturnGoal_WhenGoalExists() {
        // Arrange
        Long goalId = 1L;
        Goal expectedGoal = new Goal();
        expectedGoal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(expectedGoal);

        // Act
        Goal actualGoal = goalService.getGoalById(goalId);

        // Assert
        assertNotNull(actualGoal);
        assertEquals(expectedGoal, actualGoal);
    }

    @Test
    void getGoalById_ShouldReturnNull_WhenGoalDoesNotExist() {
        // Arrange
        Long goalId = 1L;

        when(goalRepository.findById(goalId)).thenReturn(null);

        // Act
        Goal actualGoal = goalService.getGoalById(goalId);

        // Assert
        assertNull(actualGoal);
    }

    @Test
    void getGoalsByUserId_ShouldReturnGoals_WhenGoalsExist() {
        // Arrange
        Long userId = 1L;
        Goal goal1 = new Goal();
        goal1.setUserId(userId);
        Goal goal2 = new Goal();
        goal2.setUserId(userId);

        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal1, goal2));

        // Act
        List<Goal> actualGoals = goalService.getGoalsByUserId(userId);

        // Assert
        assertNotNull(actualGoals);
        assertEquals(2, actualGoals.size());
        assertTrue(actualGoals.contains(goal1));
        assertTrue(actualGoals.contains(goal2));
    }

    @Test
    void getGoalsByUserId_ShouldReturnEmptyList_WhenGoalsDoNotExist() {
        // Arrange
        Long userId = 1L;

        when(goalRepository.findByUserId(userId)).thenReturn(List.of());

        // Act
        List<Goal> actualGoals = goalService.getGoalsByUserId(userId);

        // Assert
        assertNotNull(actualGoals);
        assertTrue(actualGoals.isEmpty());
    }

    @Test
    void updateGoal_ShouldUpdateGoal_WhenGoalExists() {
        // Arrange
        Long goalId = 1L;
        String newName = "New Vacation Savings";
        Double newTargetAmount = 15000.0;
        LocalDate newDeadline = LocalDate.of(2026, 12, 31);
        Double newCurrentAmount = 5000.0;

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setName("Vacation Savings");
        existingGoal.setTargetAmount(10000.0);
        existingGoal.setCurrentAmount(0.0);
        existingGoal.setDeadline(LocalDate.of(2025, 12, 31));

        when(goalRepository.findById(goalId)).thenReturn(existingGoal);

        // Act
        boolean result = goalService.updateGoal(goalId, newName, newTargetAmount, newDeadline, newCurrentAmount);

        // Assert
        assertTrue(result);
        assertEquals(newName, existingGoal.getName());
        assertEquals(newTargetAmount, existingGoal.getTargetAmount());
        assertEquals(newDeadline, existingGoal.getDeadline());
        assertEquals(newCurrentAmount, existingGoal.getCurrentAmount());
        Mockito.verify(goalRepository, Mockito.times(1)).save(existingGoal);
    }

    @Test
    void updateGoal_ShouldNotUpdateGoal_WhenGoalDoesNotExist() {
        // Arrange
        Long goalId = 1L;
        String newName = "New Vacation Savings";
        Double newTargetAmount = 15000.0;
        LocalDate newDeadline = LocalDate.of(2026, 12, 31);
        Double newCurrentAmount = 5000.0;

        when(goalRepository.findById(goalId)).thenReturn(null);

        // Act
        boolean result = goalService.updateGoal(goalId, newName, newTargetAmount, newDeadline, newCurrentAmount);

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteGoal_ShouldDeleteGoal_WhenGoalExists() {
        // Arrange
        Long goalId = 1L;
        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(existingGoal);

        // Act
        boolean result = goalService.deleteGoal(goalId);

        // Assert
        assertTrue(result);
        Mockito.verify(goalRepository, Mockito.times(1)).delete(goalId);
    }

    @Test
    void deleteGoal_ShouldNotDeleteGoal_WhenGoalDoesNotExist() {
        // Arrange
        Long goalId = 1L;

        when(goalRepository.findById(goalId)).thenReturn(null);

        // Act
        boolean result = goalService.deleteGoal(goalId);

        // Assert
        assertFalse(result);
    }

    @Test
    void calculateProgress_ShouldReturnProgressPercentage_WhenGoalExists() {
        // Arrange
        Long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setTargetAmount(10000.0);
        goal.setCurrentAmount(5000.0);

        when(goalRepository.findById(goalId)).thenReturn(goal);

        // Act
        double progress = goalService.calculateProgress(goalId);

        // Assert
        assertEquals(50.0, progress, 0.001);
    }

    @Test
    void calculateProgress_ShouldReturnZero_WhenGoalDoesNotExist() {
        // Arrange
        Long goalId = 1L;

        when(goalRepository.findById(goalId)).thenReturn(null);

        // Act
        double progress = goalService.calculateProgress(goalId);

        // Assert
        assertEquals(0.0, progress, 0.001);
    }

    @Test
    void updateGoalProgress_ShouldUpdateGoalProgress_WhenGoalExists() {
        // Arrange
        Long goalId = 1L;
        double amountToAdd = 5000.0;
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setCurrentAmount(0.0);

        when(goalRepository.findById(goalId)).thenReturn(goal);

        // Act
        boolean result = goalService.updateGoalProgress(goalId, amountToAdd);

        // Assert
        assertTrue(result);
        assertEquals(amountToAdd, goal.getCurrentAmount());
        Mockito.verify(goalRepository, Mockito.times(1)).save(goal);
    }

    @Test
    void updateGoalProgress_ShouldNotUpdateGoalProgress_WhenGoalDoesNotExist() {
        // Arrange
        Long goalId = 1L;
        double amountToAdd = 5000.0;

        when(goalRepository.findById(goalId)).thenReturn(null);

        // Act
        boolean result = goalService.updateGoalProgress(goalId, amountToAdd);

        // Assert
        assertFalse(result);
    }
}