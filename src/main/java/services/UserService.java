package services;

import models.User;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private Map<String, User> users = new HashMap<>();
    private User currentUser;

    private static final String DATA_FILE = "users.dat";// Файл для сохранения пользователей

    public UserService() {
        loadData();
    }

    // Регистрация пользователя
    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        saveData();
        return true;
    }

    // Авторизация пользователя
    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    // Выход из системы
    public void logout() {
        currentUser = null;
    }

    // Получение текущего авторизованного пользователя
    public User getCurrentUser() {
        return currentUser;
    }

    // Поиск пользователя по логину
    public User getUserByUsername(String username) {
        return users.get(username);
    }

    // Сохранение данных пользователей в файл
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            System.out.println("Данные пользователей успешно сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных пользователей: " + e.getMessage());
        }
    }

    // Загрузка данных пользователей из файла
    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (Map<String, User>) ois.readObject();
                System.out.println("Данные пользователей успешно загружены.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при загрузке данных пользователей: " + e.getMessage());
            }
        }
    }
}
