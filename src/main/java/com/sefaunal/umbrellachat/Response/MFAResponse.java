package com.sefaunal.umbrellachat.Response;

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
public class MFAResponse {
    private String secret;
    private String secretImageUri;
}
