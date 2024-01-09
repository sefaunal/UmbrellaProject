package com.sefaunal.umbrellaauth.Response;

import lombok.Builder;
import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-12-15
 */
@Data
@Builder
public class MFAResponse {
    private String secret;

    private String secretImageUri;
}
