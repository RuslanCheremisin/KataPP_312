package ru.kata.spring.boot_security.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String showLogoutConfirmation() {
        return "logout";
    }

    @PostMapping("/logout")
    public String performLogout() {
        return "redirect:/login?logout";
    }
}
