package utils;

import java.util.Scanner;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static double getValidatedDouble(Scanner scanner, String prompt) {
        double value;
        while (true) {
            try {
                System.out.println(prompt);
                value = Double.parseDouble(scanner.nextLine());
                if (value < 0) {
                    System.out.println("Введите положительное число.");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Пожалуйста, введите корректное число(double).");
            }
        }
    }

}

