package ru.kata.spring.boot_security.demo.Controller;

import org.springframework.security.crypto.password.PasswordEncoder;
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
import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@Validated
public class UserController {

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private UserService userService;
    @Autowired
    public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String getAllUsers(ModelMap model, HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        List<User> users = userService.getAllUsers();

//        users.forEach(u -> System.out.println("User from DB: " + u.getFirstName() + " " + u.getLastName()));
        model.addAttribute("users", users);
        return "users";
    }
    @GetMapping("/add_user")
    public String showAddUserForm(Model model) {
        if (model.containsAttribute("errors")) {
            model.addAttribute("errors", new HashMap<>());
        }
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping(value = "/add_user", produces = MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
    public String saveUser(
            @RequestParam @NotBlank @Pattern(regexp = "^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$", message = "Можно использовать только буквы и дефисы(для составных имён)!") String firstName,
            @RequestParam @Pattern(regexp = "^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$", message = "Можно использовать только буквы и дефисы(для составных фамилий)!") String lastName,
            @RequestParam @Positive @Max(120) int age,
            @RequestParam @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9]{8,}$", message = "Обязательно наличие заглавных и строчных букв, а также цифр, минимум 8 символов.") String username,
            @RequestParam @Pattern(regexp = "^[A-Za-z0-9]{8,}$", message = "Можно использовать только заглавные или строчные буквы, а также цифры, минимум 8 символов.") String password,
            Model model
    ) {
        User user = new User(firstName, lastName, age, username, passwordEncoder.encode(password));
        model.addAttribute("user", user);
        userService.addUser(user);
            return "redirect:/admin/users";
    }

    @GetMapping("/edit_user")
    public String showEditForm(@RequestParam @Positive Long id, Model model) {
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new HashMap<>());
        }
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping(value = "/edit_user", produces = MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
    public String updateUser(
            @RequestParam @Positive Long id,
            @RequestParam @NotBlank @Pattern(regexp = "^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$", message = "Можно использовать только буквы и дефисы(для составных имён)!") String firstName,
            @RequestParam @Pattern(regexp = "^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$", message = "Можно использовать только буквы и дефисы(для составных фамилий)!") String lastName,
            @RequestParam @Positive @Max(120) int age,
            @RequestParam @Pattern(regexp = "^[A-Za-z0-9]{8,}$", message = "Можно использовать только заглавные или строчные буквы, а также цифры, минимум 8 символов.") String username,
            @RequestParam @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9]{8,}$", message = "Обязательно наличие заглавных и строчных букв, а также цифр, минимум 8 символов.") String password
    ) {
            User user = userService.getUserById(id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userService.updateUser(id, user);
            return "redirect:/admin/users";
    }

    @GetMapping("/delete_user")
    public String deleteUser(@RequestParam @Positive Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
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

    @GetMapping("/add_role")
    public String showAddRoleForm(Model model) {
        if (model.containsAttribute("errors")) {
            model.addAttribute("errors", new HashMap<>());
        }
        model.addAttribute("role", new Role());
        return "add-role";
    }

    @PostMapping(value = "/add_role", produces = MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
    public String saveRole(
            @RequestParam @NotBlank @Pattern(regexp = "^[A-Z]+$", message = "Можно использовать только заглавные латинские буквы!") String name,
            Model model
    ) {
        Role role = new Role(name);
        model.addAttribute("role", role);
        roleService.addRole(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/assign_roles")
    public String showAssignRoleForm(@RequestParam @Positive Long id, Model model) {
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new HashMap<>());
        }
        User user = userService.getUserById(id);
        Set<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "assign-roles";
    }

    @PostMapping(value = "/assign_roles", produces = MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
    public String assignRole(Model model,
                             @RequestParam @Positive Long id,
                             @RequestParam @NotNull Set<Role> roles) {
        userService.assignRoles(id, roles);
        return "redirect:/admin/users";
    }

    @GetMapping("/encoding-test")
    @ResponseBody
    public String testEncoding() {
        return """
           <!DOCTYPE html>
           <html>
           <head>
               <meta charset="UTF-8">
               <title>Тест</title>
           </head>
           <body>
               <h1>Тест кодировки: Добавление пользователя</h1>
           </body>
           </html>
           """;
    }

    @GetMapping(value = "/")
    public String printWelcome() {
        return "index";
    }

    @GetMapping(value = "/user")
    public String userPage() {
        return "user";
    }
}




