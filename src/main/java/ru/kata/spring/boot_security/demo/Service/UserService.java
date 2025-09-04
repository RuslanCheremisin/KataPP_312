package ru.kata.spring.boot_security.demo.Service;

import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User addUser(User user);

    User updateUser(Long id, User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUser(Long id);

    void deleteAllUsers();


    void setRoles(Long id, Set<Role> roles);

    User getUserByUsername(String username);
}
