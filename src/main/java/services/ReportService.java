package services;

import models.*;

import java.util.Map;

public class ReportService {
    private final UserService userService;

    public ReportService(UserService userService) {
        this.userService = userService;
    }

    // Генерация отчета о бюджете
    public void generateBudgetReport() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Пожалуйста, войдите в систему для просмотра отчета.");
            return;
        }

        System.out.println("Отчёт о бюджете для пользователя: " + currentUser.getUsername());
        Wallet wallet = currentUser.getWallet();
        double totalIncome = 0;
        double totalExpense = 0;

        // Вывод информации о транзакциях
        System.out.println("\nДоходы:");
        for (Transaction transaction : wallet.getTransactions()) {
            if (transaction.isIncome()) {
                totalIncome += transaction.getAmount();
                System.out.println(" - " + transaction.getAmount() + " на категорию: " + transaction.getCategory());
            }
        }

        System.out.println("\nРасходы:");
        for (Transaction transaction : wallet.getTransactions()) {
            if (!transaction.isIncome()) {
                totalExpense += transaction.getAmount();
                System.out.println(" - " + transaction.getAmount() + " на категорию: " + transaction.getCategory());
            }
        }

        System.out.println("\nОбщий доход: " + totalIncome);
        System.out.println("Общий расход: " + totalExpense);
        System.out.println("Текущий баланс: " + (totalIncome - totalExpense));

        // Отчёт по бюджетам
        CategoryService categoryService = new CategoryService(userService);
        System.out.println("\nБюджет по категориям:");
        Map<String, Double> categoryBudgets = categoryService.getCategoryBudgets();
        for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
            String category = entry.getKey();
            double budget = entry.getValue();
            double categoryExpense = 0;
            // Подсчитываем расходы по выбранной категории
            for (Transaction transaction : wallet.getTransactions()) {
                if (!transaction.isIncome() && transaction.getCategory().equals(category)) {
                    categoryExpense += transaction.getAmount();
                }
            }
            System.out.println("Категория: " + category + ", Бюджет: " + budget + ", Оставшийся бюджет: " + (budget - categoryExpense) );
        }
    }
}
