package services;

import models.*;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class CategoryService {
    private final UserService userService;
    private Map<String, Double> categoryBudgets = new HashMap<>();
    private static final String BUDGETS_FILE = "budgets.dat"; // Файл для сохранения бюджетов

    public CategoryService(UserService userService) {
        this.userService = userService;
        loadBudgets(); // Загружаем бюджеты
    }

    // Устанавливаем бюджет для категории
    public void setBudget(String category, double budget) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Пожалуйста, войдите в систему.");
            return;
        }

        if (budget < 0) {
            System.out.println("Бюджет не может быть отрицательным.");
            return;
        }

        categoryBudgets.put(category, budget);
        saveBudgets();
        System.out.println("Бюджет для категории " + category + " установлен: " + budget);
    }

    // Проверяем текущий бюджет для категории
    public void checkBudgetLimit(String category) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Пожалуйста, войдите в систему.");
            return;
        }

        if (!categoryBudgets.containsKey(category)) {
            System.out.println("Для категории " + category + " не установлен бюджет.");
            return;
        }

        double budget = categoryBudgets.get(category);
        double totalExpense = 0;
        Wallet wallet = currentUser.getWallet();

        // Подсчитываем расходы по выбранной категории
        for (Transaction transaction : wallet.getTransactions()) {
            if (!transaction.isIncome() && transaction.getCategory().equals(category)) {
                totalExpense += transaction.getAmount();
            }
        }

        System.out.println("Бюджет для категории " + category + ": " + budget);
        System.out.println("Расходы по категории " + category + ": " + totalExpense);
        if (totalExpense > budget) {
            System.out.println("Внимание! Превышен лимит бюджета для категории " + category);
        } else {
            System.out.println("Остаток бюджета для категории " + category + ": " + (budget - totalExpense));
        }
    }

    // Получаем все бюджеты для всех категорий
    public Map<String, Double> getCategoryBudgets() {
        return new HashMap<>(categoryBudgets);
    }

    // Сохранение бюджетов в файл
    private void saveBudgets() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUDGETS_FILE))) {
            oos.writeObject(categoryBudgets);
            System.out.println("Бюджеты успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении бюджетов: " + e.getMessage());
        }
    }

    // Загрузка бюджетов из файла
    @SuppressWarnings("unchecked")
    private void loadBudgets() {
        File file = new File(BUDGETS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                categoryBudgets = (Map<String, Double>) ois.readObject();
                System.out.println("Бюджеты успешно загружены.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при загрузке бюджетов: " + e.getMessage());
            }
        } else {
            System.out.println("Файл с бюджетами не найден, создаём новый.");
        }
    }
}
