
import org.example.model.Budget;
import org.example.repository.BudgetRepository;
import org.example.service.BudgetService;
import org.example.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBudget_ShouldCreateBudget_WhenValidParametersProvided() {
        // Arrange
        Long userId = 1L;
        double amount = 1000.0;
        YearMonth period = YearMonth.now();

        Budget expectedBudget = new Budget();
        expectedBudget.setId(1L);
        expectedBudget.setUserId(userId);
        expectedBudget.setAmount(amount);
        expectedBudget.setPeriod(period);

        when(budgetRepository.save(any(Budget.class))).thenReturn(expectedBudget);

        // Act
        Budget actualBudget = budgetService.createBudget(userId, amount, period);

        // Assert
        assertNotNull(actualBudget);
        assertEquals(expectedBudget, actualBudget);
        Mockito.verify(budgetRepository, Mockito.times(1)).save(any(Budget.class));
    }

    @Test
    void createBudget_ShouldReturnNull_WhenInvalidParametersProvided() {
        // Arrange
        Long userId = null;
        double amount = 1000.0;
        YearMonth period = YearMonth.now();

        // Act
        Budget actualBudget = budgetService.createBudget(userId, amount, period);

        // Assert
        assertNull(actualBudget);
    }

    @Test
    void getBudgetById_ShouldReturnBudget_WhenBudgetExists() {
        // Arrange
        Long budgetId = 1L;
        Budget expectedBudget = new Budget();
        expectedBudget.setId(budgetId);

        when(budgetRepository.findById(budgetId)).thenReturn(expectedBudget);

        // Act
        Budget actualBudget = budgetService.getBudgetById(budgetId);

        // Assert
        assertNotNull(actualBudget);
        assertEquals(expectedBudget, actualBudget);
    }

    @Test
    void getBudgetById_ShouldReturnNull_WhenBudgetDoesNotExist() {
        // Arrange
        Long budgetId = 1L;

        when(budgetRepository.findById(budgetId)).thenReturn(null);

        // Act
        Budget actualBudget = budgetService.getBudgetById(budgetId);

        // Assert
        assertNull(actualBudget);
    }

    @Test
    void getBudgetByUserIdAndPeriod_ShouldReturnBudget_WhenBudgetExists() {
        // Arrange
        Long userId = 1L;
        YearMonth period = YearMonth.now();
        Budget expectedBudget = new Budget();
        expectedBudget.setUserId(userId);
        expectedBudget.setPeriod(period);

        when(budgetRepository.findByUserId(userId)).thenReturn(List.of(expectedBudget));

        // Act
        Budget actualBudget = budgetService.getBudgetByUserIdAndPeriod(userId, period);

        // Assert
        assertNotNull(actualBudget);
        assertEquals(expectedBudget, actualBudget);
    }

    @Test
    void getBudgetByUserIdAndPeriod_ShouldReturnNull_WhenBudgetDoesNotExist() {
        // Arrange
        Long userId = 1L;
        YearMonth period = YearMonth.now();

        when(budgetRepository.findByUserId(userId)).thenReturn(List.of());

        // Act
        Budget actualBudget = budgetService.getBudgetByUserIdAndPeriod(userId, period);

        // Assert
        assertNull(actualBudget);
    }

    @Test
    void updateBudget_ShouldUpdateBudget_WhenBudgetExists() {
        // Arrange
        Long budgetId = 1L;
        Double newAmount = 1500.0;
        Budget existingBudget = new Budget();
        existingBudget.setId(budgetId);
        existingBudget.setAmount(1000.0);

        when(budgetRepository.findById(budgetId)).thenReturn(existingBudget);

        // Act
        boolean result = budgetService.updateBudget(budgetId, newAmount);

        // Assert
        assertTrue(result);
        assertEquals(newAmount, existingBudget.getAmount());
        Mockito.verify(budgetRepository, Mockito.times(1)).save(existingBudget);
    }

    @Test
    void updateBudget_ShouldNotUpdateBudget_WhenBudgetDoesNotExist() {
        // Arrange
        Long budgetId = 1L;
        Double newAmount = 1500.0;

        when(budgetRepository.findById(budgetId)).thenReturn(null);

        // Act
        boolean result = budgetService.updateBudget(budgetId, newAmount);

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteBudget_ShouldDeleteBudget_WhenBudgetExists() {
        // Arrange
        Long budgetId = 1L;
        Budget existingBudget = new Budget();
        existingBudget.setId(budgetId);

        when(budgetRepository.findById(budgetId)).thenReturn(existingBudget);

        // Act
        boolean result = budgetService.deleteBudget(budgetId);

        // Assert
        assertTrue(result);
        Mockito.verify(budgetRepository, Mockito.times(1)).delete(budgetId);
    }

    @Test
    void deleteBudget_ShouldNotDeleteBudget_WhenBudgetDoesNotExist() {
        // Arrange
        Long budgetId = 1L;

        when(budgetRepository.findById(budgetId)).thenReturn(null);

        // Act
        boolean result = budgetService.deleteBudget(budgetId);

        // Assert
        assertFalse(result);
    }

    @Test
    void isBudgetExceeded_ShouldReturnTrue_WhenExpensesExceedBudget() {
        // Arrange
        Long userId = 1L;
        YearMonth period = YearMonth.now();
        Budget budget = new Budget();
        budget.setAmount(1000.0);

        // Mock behavior
        when(budgetService.getBudgetByUserIdAndPeriod(userId, period)).thenReturn(budget);
        when(transactionService.calculateTotalExpenses(userId, any(), any())).thenReturn(1500.0);

        // Act
        boolean result = budgetService.isBudgetExceeded(userId, period, transactionService);

        // Assert
        assertTrue(result);
    }

    @Test
    void isBudgetExceeded_ShouldReturnFalse_WhenBudgetNotSet() {
        // Arrange
        Long userId = 1L;
        YearMonth period = YearMonth.now();

        // Mock behavior
        when(budgetService.getBudgetByUserIdAndPeriod(userId, period)).thenReturn(null);

        // Act
        boolean result = budgetService.isBudgetExceeded(userId, period, transactionService);

        // Assert
        assertFalse(result);
    }
}