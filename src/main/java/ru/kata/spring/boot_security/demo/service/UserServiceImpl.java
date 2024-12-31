package ru.kata.spring.boot_security.demo.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDAO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserDAO userDAO;
    private final RoleService roleService;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserDAO userDAO, RoleService roleService, @Lazy PasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleService = roleService;
    }

    @Override
    public List<User> allUsers() {
        return userDAO.allUsers();
    }

    @Override
    @Transactional
    public void add(User user, List<Long> rolesId) {
        User userFromDB = userDAO.getByEmail(user.getEmail());
        if (userFromDB != null) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        List<Role> roles = new ArrayList<>();
        for (Long id : rolesId) {
            roles.add(roleService.getById(id));
        }
        user.setRoles(roles);

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userDAO.add(user);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userDAO.delete(user);
    }

    @Override
    @Transactional
    public void edit(User user, List <Long> rolesId) {

        User existingUser = userDAO.getById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setSurname(user.getSurname());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setSalary(user.getSalary());
        existingUser.setEmail(user.getEmail());

        List<Role> roles = new ArrayList<>();
        for (Long id : rolesId) {
            roles.add(roleService.getById(id));
        }
        existingUser.setRoles(roles);

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        userDAO.edit(existingUser);
    }

    @Override
    public User getById(Long id) {
        return userDAO.getById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.getByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthorities(user)
        );
    }
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
