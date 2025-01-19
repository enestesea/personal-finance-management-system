package models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final String category;
    private final boolean isIncome;
    private final LocalDateTime dateTime;

    public Transaction(double amount, String category, boolean isIncome) {
        this.amount = amount;
        this.category = category;
        this.isIncome = isIncome;
        this.dateTime = LocalDateTime.now();
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public boolean isIncome() {
        return isIncome;
    }


    @Override
    public String toString() {
        String type = isIncome ? "Доход" : "Расход";
        return "[" + dateTime + "] " + type + ": " + amount + ", Категория: " + category;
    }
}
