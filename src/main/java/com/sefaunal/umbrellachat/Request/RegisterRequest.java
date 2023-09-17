package com.sefaunal.umbrellachat.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author github.com/sefaunal
 * created on 2023-09-18
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
