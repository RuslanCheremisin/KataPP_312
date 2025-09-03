package ru.kata.spring.boot_security.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.UserService;

import javax.validation.constraints.Positive;

@Controller
public class UserController {
	private UserService userService;
@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(value = "/")
	public String printWelcome() {
		return "index";
	}

	@GetMapping(value = "/user")
	public String userPage(Authentication authentication, Model model) {
		String username = authentication.getName();
		model.addAttribute("currentUser", userService.getUserByUsername(username));
		return "user";
	}
	
}