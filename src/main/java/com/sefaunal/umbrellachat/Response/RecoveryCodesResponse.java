package com.sefaunal.umbrellachat.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryCodesResponse {
    private List<String> recoveryCodes;
}
