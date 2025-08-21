package ru.kata.spring.boot_security.demo.Service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    void addUser(User user);

    void updateUser(Long id, User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUser(Long id);

    void deleteAllUsers();


    void assignRoles(Long id, Set<Role> roles);

    UserDetails getUserByUsername(String username);
}
