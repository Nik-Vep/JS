package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ModelAndView userProfile() {
        ModelAndView modelAndView = new ModelAndView();
        User currentUser = userService.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        modelAndView.setViewName("forUser");
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }
}
