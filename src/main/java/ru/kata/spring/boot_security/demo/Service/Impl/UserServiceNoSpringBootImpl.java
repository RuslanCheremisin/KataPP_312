package ru.kata.spring.boot_security.demo.Service.Impl;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.DAO.Impl.UserDAONoSpringBootImpl;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceNoSpringBootImpl implements UserService {

    private UserDAO userDAO;
    @Autowired
    public UserServiceNoSpringBootImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        userDAO.addUser(user);
    }

    @Transactional
    @Override
    public void updateUser(Long id, User user) {
        userDAO.updateUser(id, user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        return userDAO.getUserByID(id);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userDAO.deleteUser(id);
    }

    @Transactional
    @Override
    public void deleteAllUsers() {
        userDAO.deleteAllUsers();
    }

    @Override
    public void setRoles(Long id, Set<Role> roles) {
        /// ///
    }

    @Override
    public UserDetails getUserByUsername(String username) {
        return null;
    }


}
