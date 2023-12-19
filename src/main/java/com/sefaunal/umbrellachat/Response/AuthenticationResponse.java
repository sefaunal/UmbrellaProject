package com.sefaunal.umbrellachat.Response;

import lombok.Builder;
import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-09-18
 */
@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean mfaEnabled;
}
