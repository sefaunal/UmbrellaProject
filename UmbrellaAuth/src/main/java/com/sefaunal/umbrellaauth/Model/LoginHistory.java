package com.sefaunal.umbrellaauth.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author github.com/sefaunal
 * @since 2023-12-04
 */
@Data
@Document
public class LoginHistory {
    @Id
    private String ID;

    private String environment;

    private String IPAddress;

    private LocalDateTime timestamp;

    private String userID;
}
