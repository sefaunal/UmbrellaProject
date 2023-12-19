package com.sefaunal.umbrellachat.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 */
@Data
public class AuthenticationRequest {
    @Email
    @NotNull
    @Size(min = 2, max = 75)
    private String email;

    @NotNull
    @Size(min = 4, max = 128)
    private String password;
}
