
import org.example.model.Transaction;
import org.example.model.TransactionType;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_ShouldCreateTransaction_WhenValidParametersProvided() {
        // Arrange
        Long userId = 1L;
        double amount = 100.0;
        String category = "Food";
        String description = "Lunch";
        LocalDateTime date = LocalDateTime.now();
        TransactionType type = TransactionType.INCOME;

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(1L);
        expectedTransaction.setUserId(userId);
        expectedTransaction.setAmount(amount);
        expectedTransaction.setCategory(category);
        expectedTransaction.setDescription(description);
        expectedTransaction.setDate(date);
        expectedTransaction.setType(type);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        // Act
        Transaction actualTransaction = transactionService.createTransaction(userId, amount, category, description, date, type);

        // Assert
        assertNotNull(actualTransaction);
        assertEquals(expectedTransaction, actualTransaction);
        Mockito.verify(transactionRepository, Mockito.times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldReturnNull_WhenInvalidParametersProvided() {
        // Arrange
        Long userId = null;
        double amount = 100.0;
        String category = "Food";
        String description = "Lunch";
        LocalDateTime date = LocalDateTime.now();
        TransactionType type = TransactionType.INCOME;

        // Act
        Transaction actualTransaction = transactionService.createTransaction(userId, amount, category, description, date, type);

        // Assert
        assertNull(actualTransaction);
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(transactionId);

        when(transactionRepository.findById(transactionId)).thenReturn(expectedTransaction);

        // Act
        Transaction actualTransaction = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(actualTransaction);
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    void getTransactionById_ShouldReturnNull_WhenTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 1L;

        when(transactionRepository.findById(transactionId)).thenReturn(null);

        // Act
        Transaction actualTransaction = transactionService.getTransactionById(transactionId);

        // Assert
        assertNull(actualTransaction);
    }

    @Test
    void getAllTransactionsByUserId_ShouldReturnTransactions_WhenTransactionsExist() {
        // Arrange
        Long userId = 1L;
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2));

        // Act
        List<Transaction> actualTransactions = transactionService.getAllTransactionsByUserId(userId);

        // Assert
        assertNotNull(actualTransactions);
        assertEquals(2, actualTransactions.size());
        assertTrue(actualTransactions.contains(transaction1));
        assertTrue(actualTransactions.contains(transaction2));
    }

    @Test
    void getAllTransactionsByUserId_ShouldReturnEmptyList_WhenTransactionsDoNotExist() {
        // Arrange
        Long userId = 1L;

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        // Act
        List<Transaction> actualTransactions = transactionService.getAllTransactionsByUserId(userId);

        // Assert
        assertNotNull(actualTransactions);
        assertTrue(actualTransactions.isEmpty());
    }

    @Test
    void getTransactionsByUserIdAndDateRange_ShouldReturnTransactions_WhenTransactionsExistWithinRange() {
        // Arrange
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        transaction1.setDate(startDate.plusDays(3)); // Within range
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);
        transaction2.setDate(endDate.minusDays(3)); // Within range
        Transaction transaction3 = new Transaction();
        transaction3.setUserId(userId);
        transaction3.setDate(startDate.minusDays(3)); // Outside range

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2, transaction3));

        // Act
        List<Transaction> actualTransactions = transactionService.getTransactionsByUserIdAndDateRange(userId, startDate, endDate);

        // Assert
        assertNotNull(actualTransactions);
        assertEquals(2, actualTransactions.size());
        assertTrue(actualTransactions.contains(transaction1));
        assertTrue(actualTransactions.contains(transaction2));
        assertFalse(actualTransactions.contains(transaction3));
    }

    @Test
    void updateTransaction_ShouldUpdateTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Double newAmount = 150.0;
        String newCategory = "Transport";
        String newDescription = "Bus Ticket";
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setAmount(100.0);
        existingTransaction.setCategory("Food");
        existingTransaction.setDescription("Lunch");

        when(transactionRepository.findById(transactionId)).thenReturn(existingTransaction);

        // Act
        boolean result = transactionService.updateTransaction(transactionId, newAmount, newCategory, newDescription);

        // Assert
        assertTrue(result);
        assertEquals(newAmount, existingTransaction.getAmount());
        assertEquals(newCategory, existingTransaction.getCategory());
        assertEquals(newDescription, existingTransaction.getDescription());
        Mockito.verify(transactionRepository, Mockito.times(1)).save(existingTransaction);
    }

    @Test
    void updateTransaction_ShouldNotUpdateTransaction_WhenTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 1L;
        Double newAmount = 150.0;
        String newCategory = "Transport";
        String newDescription = "Bus Ticket";

        when(transactionRepository.findById(transactionId)).thenReturn(null);

        // Act
        boolean result = transactionService.updateTransaction(transactionId, newAmount, newCategory, newDescription);

        // Assert
        assertFalse(result);
    }

    @Test
    void deleteTransaction_ShouldDeleteTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);

        when(transactionRepository.findById(transactionId)).thenReturn(existingTransaction);

        // Act
        boolean result = transactionService.deleteTransaction(transactionId);

        // Assert
        assertTrue(result);
        Mockito.verify(transactionRepository, Mockito.times(1)).delete(transactionId);
    }

    @Test
    void deleteTransaction_ShouldNotDeleteTransaction_WhenTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 1L;

        when(transactionRepository.findById(transactionId)).thenReturn(null);

        // Act
        boolean result = transactionService.deleteTransaction(transactionId);

        // Assert
        assertFalse(result);
    }

    @Test
    void calculateTotalIncome_ShouldReturnSumOfIncomeTransactions_WhenTransactionsExist() {
        // Arrange
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        transaction1.setAmount(100.0);
        transaction1.setType(TransactionType.INCOME);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);
        transaction2.setAmount(200.0);
        transaction2.setType(TransactionType.INCOME);
        Transaction transaction3 = new Transaction();
        transaction3.setUserId(userId);
        transaction3.setAmount(300.0);
        transaction3.setType(TransactionType.EXPENSE);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2, transaction3));

        // Act
        double totalIncome = transactionService.calculateTotalIncome(userId, startDate, endDate);

        // Assert
        assertEquals(300.0, totalIncome, 0.001);
    }

    @Test
    void calculateTotalExpenses_ShouldReturnSumOfExpenseTransactions_WhenTransactionsExist() {
        // Arrange
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        transaction1.setAmount(100.0);
        transaction1.setType(TransactionType.EXPENSE);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);
        transaction2.setAmount(200.0);
        transaction2.setType(TransactionType.EXPENSE);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2));

        // Act
        double totalExpenses = transactionService.calculateTotalExpenses(userId, startDate, endDate);

        // Assert
        assertEquals(300.0, totalExpenses, 0.001);
    }

    @Test
    void calculateBalance_ShouldReturnDifferenceBetweenIncomeAndExpenses_WhenTransactionsExist() {
        // Arrange
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        transaction1.setAmount(100.0);
        transaction1.setType(TransactionType.INCOME);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);
        transaction2.setAmount(200.0);
        transaction2.setType(TransactionType.EXPENSE);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2));

        // Act
        double balance = transactionService.calculateBalance(userId);

        // Assert
        assertEquals(-100.0, balance, 0.001);
    }

    @Test
    void calculateExpensesByCategory_ShouldReturnMapOfCategoriesAndTotals_WhenExpenseTransactionsExist() {
        // Arrange
        Long userId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        transaction1.setAmount(100.0);
        transaction1.setCategory("Food");
        transaction1.setType(TransactionType.EXPENSE);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);
        transaction2.setAmount(200.0);
        transaction2.setCategory("Transport");
        transaction2.setType(TransactionType.EXPENSE);

        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction1, transaction2));

        // Act
        Map<String, Double> expensesByCategory = transactionService.calculateExpensesByCategory(userId, startDate, endDate);

        // Assert
        assertNotNull(expensesByCategory);
        assertEquals(2, expensesByCategory.size());
        assertEquals(100.0, expensesByCategory.get("Food"), 0.001);
        assertEquals(200.0, expensesByCategory.get("Transport"), 0.001);
    }
}