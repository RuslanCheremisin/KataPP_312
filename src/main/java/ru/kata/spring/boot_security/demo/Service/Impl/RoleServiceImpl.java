package ru.kata.spring.boot_security.demo.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DAO.RoleDAO;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleDAO roleDAO;

    @Autowired
    public RoleServiceImpl(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }
    @Override
    public Set<Role> getAllRoles() {
        return new HashSet<>(roleDAO.findAll());
    }

    @Override
    public Set<Role> getRolesByIds(Set<Long> roleIds) {
        return new HashSet<>(roleDAO.findAllById(roleIds));
    }

    @Override
    public void addRole(Role role) {
        roleDAO.save(role);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleDAO.findRoleByName(name);
    }

}
