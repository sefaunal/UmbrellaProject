package com.sefaunal.umbrellachat.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author github.com/sefaunal
 * @since 2023-12-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    @NotNull
    @Size(min = 6, max = 6)
    private String mfaCode;
}
