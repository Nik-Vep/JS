package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {
    List<User> allUsers();
    void add(User user, List<Long> rolesId);
    void delete(User user);
    void edit(User user, List<Long> rolesId);
    User getById(Long id);
    User getByName(String name);
    User getByEmail(String email);
}
