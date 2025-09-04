package ru.kata.spring.boot_security.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.validation.Validation;
import javax.validation.Validator;

@Component
public class CustomValidator {

    @Bean
    public Validator getValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
