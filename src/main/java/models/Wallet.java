package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private double balance;
    private final List<Transaction> transactions;

    public Wallet() {
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    // Добавление транзакции (доход/расход)
    public void addTransaction(double amount, String category, boolean isIncome) {
        transactions.add(new Transaction(amount, category, isIncome));

        // Если это доход - добавляем к балансу
        if (isIncome) {
            balance += amount;
        } else {
            // Если это расход - вычитаем из баланса
            balance -= amount;
        }
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "Кошелек: Баланс = " + balance;
    }
}
