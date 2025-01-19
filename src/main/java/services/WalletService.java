package services;

import models.*;

import java.io.*;
import java.util.List;

public class WalletService {
    private final UserService userService;
    private final CategoryService categoryService;
    private static final String TRANSACTIONS_FILE = "wallet_transactions.dat"; // Файл для сохранения транзакций
    private static final String NO_AUTH = "Пользователь не авторизован.";

    public WalletService(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    // Добавление дохода
    public void addIncome(double amount, String category) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println(NO_AUTH);
            return;
        }

        Wallet wallet = currentUser.getWallet();
        wallet.addTransaction(amount, category, true); // true для дохода
        System.out.println("Доход добавлен: " + amount + " в категорию \"" + category + "\"");

        // Проверка превышения доходов и расходов
        checkIncomeVsExpenses();

        // Проверка бюджета для категории
        categoryService.checkBudgetLimit(category);
    }

    // Добавление расхода
    public void addExpense(double amount, String category) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println(NO_AUTH);
            return;
        }

        Wallet wallet = currentUser.getWallet();
        wallet.addTransaction(amount, category, false); // false для расхода
        System.out.println("Расход добавлен: " + amount + " в категорию \"" + category + "\"");

        // Проверка превышения бюджета для категории
        categoryService.checkBudgetLimit(category);

        // Проверка превышения расходов над доходами
        checkIncomeVsExpenses();
    }

    // Перевод между пользователями
    public void transferBetweenUsers(String recipientUsername, double amount) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println(NO_AUTH);
            return;
        }

        User recipient = userService.getUserByUsername(recipientUsername);
        if (recipient == null) {
            System.out.println("Пользователь с логином \"" + recipientUsername + "\" не найден.");
            return;
        }

        Wallet senderWallet = currentUser.getWallet();
        Wallet recipientWallet = recipient.getWallet();

        if (senderWallet.getBalance() < amount) {
            System.out.println("Недостаточно средств для перевода.");
            return;
        }

        senderWallet.addTransaction(amount, "Перевод пользователю " + recipientUsername, false);
        recipientWallet.addTransaction(amount, "Перевод от пользователя " + currentUser.getUsername(), true);

        System.out.println("Перевод успешно выполнен. Сумма: " + amount + ", получатель: " + recipientUsername);
    }

    // Просмотр всех транзакций текущего пользователя
    public void viewTransactions() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println(NO_AUTH);
            return;
        }

        Wallet wallet = currentUser.getWallet();
        List<Transaction> transactions = wallet.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("Нет транзакций.");
            return;
        }

        System.out.println("Список транзакций:");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }


    // Метод для сохранения транзакций в файл
    public void saveTransactions() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println(NO_AUTH);
            return;
        }
        Wallet wallet = currentUser.getWallet();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            oos.writeObject(wallet.getTransactions());
            System.out.println("Транзакции успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении транзакций: " + e.getMessage());
        }
    }

    // Метод для загрузки транзакций из файла
    public void loadTransactions() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println(NO_AUTH);
            return;
        }
        Wallet wallet = currentUser.getWallet();
        File file = new File(TRANSACTIONS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Transaction> transactions = (List<Transaction>) ois.readObject();
                wallet.getTransactions().clear();
                wallet.getTransactions().addAll(transactions);
                System.out.println("Транзакции успешно загружены.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при загрузке транзакций: " + e.getMessage());
            }
        }
    }

    // Проверка превышения доходов и расходов
    private void checkIncomeVsExpenses() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Wallet wallet = currentUser.getWallet();
        double totalIncome = 0;
        double totalExpense = 0;

        // Подсчитываем все доходы и расходы
        for (Transaction transaction : wallet.getTransactions()) {
            if (transaction.isIncome()) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }

        // Если расходы превышают доходы
        if (totalExpense > totalIncome) {
            System.out.println("Внимание! Ваши расходы превысили доходы на сумму: " + (totalExpense - totalIncome));
        } else {
            System.out.println("Ваши расходы в пределах доходов.");
        }
    }
}
