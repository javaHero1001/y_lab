package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import lombok.extern.log4j.Log4j2;
import org.example.model.Transaction;
import org.example.model.TransactionType;
import org.example.model.User;
import org.example.repository.BudgetRepository;
import org.example.repository.GoalRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.AdminService;
import org.example.service.BudgetService;
import org.example.service.GoalService;
import org.example.service.TransactionService;
import org.example.service.UserService;
import org.example.service.NotificationService;

@Log4j2
public class FinanceManager {
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);

    private static UserRepository userRepository = new UserRepository();
    private static TransactionRepository transactionRepository = new TransactionRepository();
    private static UserService userService = new UserService(userRepository);
    private static TransactionService transactionService = new TransactionService(transactionRepository);
    private static BudgetService budgetService = new BudgetService(new BudgetRepository(), transactionService);
    private static GoalService goalService = new GoalService(new GoalRepository());
    private static NotificationService notificationService = new NotificationService();
    private static AdminService adminService = new AdminService(userRepository, transactionRepository);

    public static void main(String[] args) {
        System.out.println("Starting application...");
        while (true) {
            if (isUserLoggedIn()) {
                showUserMenu();
            } else {
                showGuestMenu();
            }
        }
    }

    private static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    private static void showGuestMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Регистрация");
        System.out.println("2. Авторизация");
        System.out.println("3. Выход");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                registerUser();
                break;
            case "2":
                loginUser();
                break;
            case "3":
                System.out.println("До свидания!");
                System.exit(0);
            default:
                System.out.println("Неверный выбор.");
        }
    }

    private static void showUserMenu() {
        System.out.println("\n--- Меню пользователя ---");
        System.out.println("1. Добавить транзакцию");
        System.out.println("2. Редактировать профиль");
        System.out.println("3. Удалить аккаунт");
        System.out.println("4. Установить бюджет");
        System.out.println("5. Установить цель");
        System.out.println("6. Просмотреть транзакции");
        System.out.println("7. Просмотреть статистику");
        System.out.println("8. Выйти из аккаунта");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                addTransaction();
                break;
            case "2":
                editProfile();
                break;
            case "3":
                deleteAccount();
                break;
            case "4":
                setBudget();
                break;
            case "5":
                setGoal();
                break;
            case "6":
                showTransactions();
                break;
            case "7":
                showStatistics();
                break;
            case "8":
                logout();
                break;
            default:
                System.out.println("Неверный выбор.");
        }
    }

    private static void registerUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        User newUser = userService.registerUser(name, email, password, false); // По умолчанию не админ
        if (newUser != null) {
            System.out.println("Регистрация прошла успешно! Можете войти.");
        } else {
            System.out.println("Не удалось зарегистрировать пользователя. Возможно, email уже используется.");
        }
    }

    private static void loginUser() {
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        User user = userService.loginUser(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Вход выполнен успешно, " + user.getName() + "!");
        } else {
            System.out.println("Неверный email или пароль.");
        }
    }

    private static void addTransaction() {
        System.out.print("Сумма: ");
        double amount = 0;
        try {
            amount = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Неверный формат суммы.");
            scanner.nextLine(); // Сброс неправильного ввода
            return;
        }
        scanner.nextLine();

        System.out.print("Категория: ");
        String category = scanner.nextLine();
        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Тип транзакции (Доход/Расход): ");
        String inputType = scanner.nextLine().trim().toUpperCase(); // Приводим ввод к верхнему регистру

        TransactionType type;
        try {
            type = TransactionType.valueOf(inputType); // Пробуем преобразовать ввод в значение перечисления
        } catch (IllegalArgumentException e) {
            System.out.println("Недопустимый тип транзакции. Используйте 'ДОХОД' или 'РАСХОД'.");
            return;
        }

        Transaction transaction = transactionService.createTransaction(
                currentUser.getId(),
                amount,
                category,
                description,
                LocalDateTime.now(),
                type
        );

        if (transaction != null) {
            System.out.println("Транзакция добавлена.");
        } else {
            System.out.println("Не удалось добавить транзакцию.");
        }
    }

    private static void editProfile() {
        System.out.print("Введите новое имя (оставьте пустым для сохранения текущего): ");
        String newName = scanner.nextLine();
        System.out.print("Введите новый email (оставьте пустым для сохранения текущего): ");
        String newEmail = scanner.nextLine();
        System.out.print("Введите новый пароль (оставьте пустым для сохранения текущего): ");
        String newPassword = scanner.nextLine();

        boolean updated = userService.updateUser(currentUser.getId(), newName, newEmail, newPassword);
        if (updated) {
            System.out.println("Профиль обновлен успешно.");
        } else {
            System.out.println("Не удалось обновить профиль.");
        }
    }

    private static void deleteAccount() {
        if (userService.deleteUser(currentUser.getId())) {
            currentUser = null;
            System.out.println("Аккаунт успешно удален.");
        } else {
            System.out.println("Не удалось удалить аккаунт.");
        }
    }

    private static void setBudget() {
        System.out.print("Введите месячный бюджет: ");
        double budget = 0;
        try {
            budget = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Неверный формат бюджета.");
            scanner.nextLine(); // Сброс неправильного ввода
            return;
        }
        scanner.nextLine(); // consume newline
        budgetService.createBudget(currentUser.getId(), budget, YearMonth.now());
        System.out.println("Бюджет установлен успешно.");
    }

    private static void setGoal() {
        System.out.print("Введите цель накопления: ");
        double targetAmount = 0;
        try {
            targetAmount = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Неверный формат цели.");
            scanner.nextLine(); // Сброс неправильного ввода
            return;
        }
        scanner.nextLine(); // consume newline
        LocalDate deadline = LocalDate.now();
        System.out.print("Введите срок выполнения цели (YYYY-MM-DD): ");
        try {
            deadline = LocalDate.parse(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Неверный формат даты.");
            return;
        }
        goalService.createGoal(currentUser.getId(), "Цель накопления", targetAmount, deadline);
        System.out.println("Цель установлена успешно.");
    }

    private static void showTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactionsByUserId(currentUser.getId());
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void showStatistics() {
        // Получаем текущий месяц для начала и конца периода
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);

        // Рассчитываем общий доход и общие расходы за текущий месяц
        double totalIncome = transactionService.calculateTotalIncome(currentUser.getId(), startOfMonth, endOfMonth);
        double totalExpenses = transactionService.calculateTotalExpenses(currentUser.getId(), startOfMonth, endOfMonth);

        // Вычисляем текущий баланс
        double balance = transactionService.calculateBalance(currentUser.getId());

        // Выводим результаты
        System.out.println("Суммарный доход за месяц: " + totalIncome);
        System.out.println("Суммарные расходы за месяц: " + totalExpenses);
        System.out.println("Текущий баланс: " + balance);
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Вы вышли из аккаунта.");
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Административное меню ---");
            System.out.println("1. Просмотреть список пользователей");
            System.out.println("2. Просмотреть транзакции пользователя");
            System.out.println("3. Заблокировать пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("5. Вернуться в основное меню");
            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewUserTransactions();
                    break;
                case "3":
                    blockUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "5":
                    return; // Выход в главное меню
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }

    private static void viewAllUsers() {
        List<User> users = adminService.getUsers();
        for (User user : users) {
            System.out.println("ID: " + user.getId() + ", Имя: " + user.getName() + ", Email: " + user.getEmail() + ", Заблокирован: " + user.isBlocked());
        }
    }

    private static void viewUserTransactions() {
        System.out.print("Введите ID пользователя: ");
        long userId = Long.parseLong(scanner.nextLine()); // Преобразуем строку в long
        List<Transaction> transactions = adminService.getUserTransactions(userId);
        for (Transaction transaction : transactions) {
            System.out.println("ID: " + transaction.getId() + ", Сумма: " + transaction.getAmount() + ", Категория: " + transaction.getCategory() + ", Описание: " + transaction.getDescription() + ", Дата: " + transaction.getDate() + ", Тип: " + transaction.getType());
        }
    }

    private static void blockUser() {
        System.out.print("Введите ID пользователя для блокировки: ");
        long userId = Long.parseLong(scanner.nextLine()); // Преобразуем строку в long
        if (adminService.blockUser(userId)) {
            System.out.println("Пользователь успешно заблокирован.");
        } else {
            System.out.println("Не удалось заблокировать пользователя.");
        }
    }

    private static void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        long userId = Long.parseLong(scanner.nextLine()); // Преобразуем строку в long
        if (userId == 0) { // Если пользователь не введен, выход
            return;
        }

        // Проверка, является ли текущий пользователь администратором
        if (!currentUser.isAdmin()) {
            System.out.println("У вас недостаточно прав для удаления пользователя.");
            return;
        }

        // Проверка, является ли пользователь заблокированным
        User userToDelete = userRepository.findById(userId);
        if (userToDelete != null && userToDelete.isBlocked()) {
            System.out.println("Пользователь уже заблокирован и не может быть удален.");
            return;
        }

        // Удаление пользователя и его транзакций
        boolean isDeleted = adminService.deleteUser(userId);
        if (isDeleted) {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            for (Transaction transaction : transactions) {
                transactionService.deleteTransaction(transaction.getId());
            }
            System.out.println("Пользователь успешно удален.");
        } else {
            System.out.println("Не удалось удалить пользователя.");
        }
    }
}