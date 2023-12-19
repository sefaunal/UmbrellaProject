package com.sefaunal.umbrellachat.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
@Data
@Builder
public class RecoveryCodesResponse {
    private List<String> recoveryCodes;
}
