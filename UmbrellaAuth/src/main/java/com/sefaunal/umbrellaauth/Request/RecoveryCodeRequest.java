package com.sefaunal.umbrellaauth.Request;

import lombok.Data;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
@Data
public class RecoveryCodeRequest {
    private String recoveryCode;
}
