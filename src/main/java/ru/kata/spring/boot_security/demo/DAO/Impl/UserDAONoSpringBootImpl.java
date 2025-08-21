package ru.kata.spring.boot_security.demo.DAO.Impl;

import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.Model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDAONoSpringBootImpl implements UserDAO {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Override
    public void addUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void updateUser(Long id, User newUser) {
        User user = getUserByID(id);
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setAge(newUser.getAge());
        entityManager.merge(user);
        entityManager.flush();
    }

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User getUserByID(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void deleteUser(Long id) {
        entityManager.createQuery("DELETE FROM User u WHERE u.id = :id").setParameter("id", id).executeUpdate();
    }

    @Override
    public void deleteAllUsers() {
        entityManager.createNativeQuery("TRUNCATE TABLE users").executeUpdate();
    }
}
