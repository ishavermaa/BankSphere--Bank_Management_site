package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        user.setRole(User.Role.CUSTOMER);
        user.setStatus(User.Status.ACTIVE);
        userService.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // Route to appropriate dashboard based on role
        switch (user.getRole()) {
            case ADMIN:
                return "admin-dashboard";
            case EMPLOYEE:
                return "employee-dashboard";
            case CUSTOMER:
            default:
                return "dashboard";
        }
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null || user.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "admin-dashboard";
    }

    @GetMapping("/employee-dashboard")
    public String employeeDashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null || user.getRole() != User.Role.EMPLOYEE) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "employee-dashboard";
    }
}