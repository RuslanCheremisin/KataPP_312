package ru.kata.spring.boot_security.demo.Service.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.DAO.UserDAOSpringBoot;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Primary
public class UserServiceSpringBoot implements UserService {

    private UserDAOSpringBoot userDAOSpringBoot;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceSpringBoot(UserDAOSpringBoot userDAOSpringBoot, PasswordEncoder passwordEncoder) {
        this.userDAOSpringBoot = userDAOSpringBoot;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        userDAOSpringBoot.save(user);
    }

    @Transactional
    @Override
    public void updateUser(Long id, User newUser) {
        User existingUser;
        try {
            existingUser = userDAOSpringBoot.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Entity with ID " + id + " not found");
        }

        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        existingUser.setAge(newUser.getAge());
        existingUser.setUsername(newUser.getUsername());
        String newPassword = newUser.getPassword();

        if (!newUser.getPassword().equals(existingUser.getPassword()) &&
                newPassword != null &&
                !newPassword.isEmpty() &&
                !passwordEncoder.matches(newPassword, existingUser.getPassword())) {
                String newPasswordHash = passwordEncoder.encode(newPassword);
                existingUser.setPassword(newPasswordHash);

        }
        existingUser.setRoles(newUser.getRoles());
        userDAOSpringBoot.save(existingUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userDAOSpringBoot.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        try {
        return userDAOSpringBoot.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Entity with ID " + id + " not found");
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User existingUser;
        try {
            existingUser = userDAOSpringBoot.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Entity with ID " + id + " not found");
        }
        userDAOSpringBoot.delete(existingUser);
    }

    @Transactional
    @Override
    public void deleteAllUsers() {
        userDAOSpringBoot.deleteAll();
    }

    @Override
    public void setRoles(Long id, Set<Role> roles) {
        User user = userDAOSpringBoot.findById(id).get();
        user.setRoles(new HashSet<>());
        user.setRoles(roles);
//        userDAOSpringBoot.save(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDAOSpringBoot.findByUsername(username);
    }



}
