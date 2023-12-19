package com.sefaunal.umbrellachat.Request;

import com.sefaunal.umbrellachat.Annotation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 */
@Data
public class RegisterRequest {
    @NotNull
    @Size(min = 2, max = 75)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 75)
    private String lastName;

    @Email
    @NotNull
    @UniqueEmail
    @Size(min = 2, max = 75)
    private String email;

    @NotNull
    @Size(min = 4, max = 128)
    private String password;
}
