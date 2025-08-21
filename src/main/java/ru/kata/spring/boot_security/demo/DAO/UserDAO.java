package ru.kata.spring.boot_security.demo.DAO;

import ru.kata.spring.boot_security.demo.Model.User;

import java.util.List;

public interface UserDAO {
    void addUser(User user);
    void updateUser(Long id, User user);
    List<User> getAllUsers();
    User getUserByID(Long id);
    void deleteUser(Long id);
    void deleteAllUsers();


}
