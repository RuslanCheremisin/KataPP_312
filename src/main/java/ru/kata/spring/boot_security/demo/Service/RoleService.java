package ru.kata.spring.boot_security.demo.Service;

import ru.kata.spring.boot_security.demo.Model.Role;

import java.util.Set;

public interface RoleService {
    void addRole(Role role);

    Role getRoleByName(String name);

    Set<Role> getAllRoles();
}
