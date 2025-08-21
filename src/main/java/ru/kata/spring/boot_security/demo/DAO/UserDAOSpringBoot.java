package ru.kata.spring.boot_security.demo.DAO;

import ru.kata.spring.boot_security.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAOSpringBoot extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
