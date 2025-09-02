package ru.kata.spring.boot_security.demo.Controller;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.Model.Role;
import ru.kata.spring.boot_security.demo.Model.User;
import ru.kata.spring.boot_security.demo.Service.RoleService;
import ru.kata.spring.boot_security.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.*;
import java.util.*;

@Controller
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final RoleService roleService;
    private UserService userService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllUsers(ModelMap model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("currentUser", currentUser);
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        Set<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "admin-page";
    }


    @PostMapping(value = "/users/add", produces = MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
    public String addUser(
            @RequestParam String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam int age,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Set <Long> roles,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();

        if (firstName == null || firstName.isBlank() ||
                !firstName.matches("^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$")) {
            errors.put("firstName", "Можно использовать только буквы и дефисы (для составных имён)!");
        }
        if (lastName != null && !lastName.isBlank() &&
                !lastName.matches("^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$")) {
            errors.put("lastName", "Можно использовать только буквы и дефисы (для составных фамилий)!");
        }
        if (age <= 0 || age > 120) {
            errors.put("age", "Возраст должен быть от 1 до 120");
        }
        if (username == null || !username.matches("^[A-Za-z0-9]{8,}$")) {
            errors.put("username", "Можно использовать только буквы и цифры, минимум 8 символов.");
        }
        if (password != null && !password.isBlank() &&
                !password.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9]{8,}$")) {
            errors.put("password", "Пароль должен содержать заглавные и строчные буквы, а также цифры, минимум 8 символов.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setUsername(username);
        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);

            User back = user;
            back.setFirstName(firstName);
            back.setLastName(lastName);
            back.setAge(age);
            back.setUsername(username);
            redirectAttributes.addFlashAttribute("user", back);
            redirectAttributes.addFlashAttribute("roles", roleService.getAllRoles());

            return "redirect:/admin#add-user-tab";
        }


        if (roles == null) {
            roles = new HashSet<>();
        }
        user.setRoles(roleService.getRolesByIds(roles));
        userService.addUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/users/edit")
    public String updateUser(
            @RequestParam Long id,
            @RequestParam String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam int age,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Set <Long> roles,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();

        if (firstName == null || firstName.isBlank() ||
                !firstName.matches("^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$")) {
            errors.put("firstName", "В имени разрешено использовать только буквы и дефисы (для составных имён)!");
        }
        if (lastName != null && !lastName.isBlank() &&
                !lastName.matches("^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$")) {
            errors.put("lastName", "В фамилии разрешено использовать только буквы и дефисы (для составных фамилий)!");
        }
        if (age <= 0 || age > 120) {
            errors.put("age", "Возраст должен быть от 1 до 120");
        }
        if (username == null || !username.matches("^[A-Za-z0-9]{8,}$")) {
            errors.put("username", "В логине разрешено использовать только буквы и цифры, минимум 8 символов.");
        }
        if (password != null && !password.isBlank() &&
                !password.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9]{8,}$")) {
            errors.put("password", "Пароль должен содержать заглавные и строчные буквы, а также цифры, минимум 8 символов.");
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);

            User back = userService.getUserById(id);
            back.setFirstName(firstName);
            back.setLastName(lastName);
            back.setAge(age);
            back.setUsername(username);
            redirectAttributes.addFlashAttribute("editUser", back);
            redirectAttributes.addFlashAttribute("roles", roleService.getAllRoles());

            redirectAttributes.addFlashAttribute("openEditModal", true);

            return "redirect:/admin";
        }

        User userFromDB = userService.getUserById(id);
        User userFromForm = new User();
        userFromForm.setFirstName(firstName);
        userFromForm.setLastName(lastName);
        userFromForm.setAge(age);
        userFromForm.setUsername(username);
        if (password != null && !password.isBlank()) {
            userFromForm.setPassword(password);
        } else {
            userFromForm.setPassword(userFromDB.getPassword());
        }

        if (roles == null) {
            roles = new HashSet<>();
        }
        userFromForm.setRoles(roleService.getRolesByIds(roles));
        userService.updateUser(id, userFromForm);

        return "redirect:/admin";
    }


    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam @Positive Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/roles")
    public String getAllroles(ModelMap model, HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Set<Role> roles = roleService.getAllRoles();
        roles.forEach(r -> System.out.println("User from DB: " + r.getName()));
        model.addAttribute("roles", roles);
        return "roles";
    }

    @GetMapping("/roles/add")
    public String showAddRoleForm(Model model) {
        if (model.containsAttribute("errors")) {
            model.addAttribute("errors", new HashMap<>());
        }
        model.addAttribute("role", new Role());
        return "add-role";
    }

    @PostMapping(value = "/roles/add", produces = MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
    public String saveRole(
            @RequestParam @NotBlank @Pattern(regexp = "^[A-Z]+$", message = "Можно использовать только заглавные латинские буквы!") String name,
            Model model
    ) {
        Role role = new Role(name);
        model.addAttribute("role", role);
        roleService.addRole(role);
        return "redirect:/admin/roles";
    }


}




