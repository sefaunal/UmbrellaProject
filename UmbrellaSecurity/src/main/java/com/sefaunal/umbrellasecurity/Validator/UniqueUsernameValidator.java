package com.sefaunal.umbrellasecurity.Validator;

import com.sefaunal.umbrellasecurity.Annotation.UniqueUsername;
import com.sefaunal.umbrellasecurity.Service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

/**
 * @author github.com/sefaunal
 * @since 2023-12-29
 */
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserService userService;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {}

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return userService.isUsernameFree(username);
    }
}
