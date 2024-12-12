package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;


    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public ModelAndView allUsers() {
        List<User> users = userService.allUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("userList", users);

        User currentUser = userService.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        modelAndView.setViewName("users");
        modelAndView.addObject("currentUser", currentUser);
        modelAndView.addObject("allRoles", roleService.allRoles());

        return modelAndView;
    }

    @GetMapping("/edit")
    public ModelAndView editPage(@RequestParam("id") Long id) {
        User user = userService.getById(id);
        List<Role> allRoles = roleService.allRoles();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allRoles", allRoles);
        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView editUser(@ModelAttribute("user") User user,
                                 @RequestParam List<Long> rolesId) {
        ModelAndView modelAndView = new ModelAndView();
        userService.edit(user, rolesId);
        modelAndView.setViewName("redirect:/api/admin/");
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView addPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("userAdd", new User());
        modelAndView.addObject("allRoles", roleService.allRoles());
        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView addUser(@ModelAttribute("userAdd") User user,
                                @RequestParam List<Long> rolesId) {
        ModelAndView modelAndView = new ModelAndView();
        userService.add(user, rolesId);
        modelAndView.setViewName("redirect:/api/admin/");
        return modelAndView;
    }

    @PostMapping("/delete")
    public ModelAndView deleteUser(@RequestParam("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/api/admin/");
        userService.delete(userService.getById(id));
        return modelAndView;
    }

    @GetMapping("/getOne")
    @ResponseBody
    public User getOne(Long id) {
        return userService.getById(id);
    }
}



