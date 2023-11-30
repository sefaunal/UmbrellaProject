package com.sefaunal.umbrellachat.Validator;

import com.sefaunal.umbrellachat.Annotation.UniqueEmail;
import com.sefaunal.umbrellachat.Service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

/**
 * @author github.com/sefaunal
 * @since 2023-11-30
 */

@AllArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return userService.findUserByMail(email).isEmpty();
    }
}
