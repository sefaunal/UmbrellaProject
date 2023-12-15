package com.sefaunal.umbrellachat.Model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author github.com/sefaunal
 * @since 2023-12-04
 */
@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginHistory {
    @Id
    private String ID;

    private String environment;

    private String IPAddress;

    private LocalDateTime timestamp;

    private String userID;
}
