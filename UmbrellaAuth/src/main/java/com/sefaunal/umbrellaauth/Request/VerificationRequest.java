package com.sefaunal.umbrellaauth.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-12-15
 */
@Data
public class VerificationRequest {
    @NotNull
    @Size(min = 6, max = 6)
    private String mfaCode;
}
