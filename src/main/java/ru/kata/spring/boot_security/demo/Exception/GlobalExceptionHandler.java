package ru.kata.spring.boot_security.demo.Exception;

import ru.kata.spring.boot_security.demo.Model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public String processValidationErrors(ConstraintViolationException ex, Model model, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String targetView = "add-user";
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            if (fieldName.startsWith("arg")) {
                int paramIndex = Integer.parseInt(fieldName.substring(3));
                fieldName = getParameterNameForIndex(paramIndex);
            } else if (propertyPath.contains("saveUser") || propertyPath.contains("updateUser")) {
                fieldName = fieldName.toLowerCase();
            }
            errors.put(fieldName, violation.getMessage());

            if (propertyPath.contains("updateUser")) {
                targetView = "edit-user";
            }
        }
        User user = new User();
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setAge(Integer.parseInt(request.getParameter("age")));
        model.addAttribute("user", user);
        model.addAttribute("errors", errors);
        return targetView;
    }

    private String getParameterNameForIndex(int index) {
        switch(index) {
            case 0: return "firstName";
            case 1: return "lastName";
            case 2: return "age";
            case 3: return "id";
            default: return "param" + index;
        }
    }

}