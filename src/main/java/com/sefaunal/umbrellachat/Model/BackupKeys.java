package com.sefaunal.umbrellachat.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
@Data
@Document
public class BackupKeys {
    @Id
    private String ID;

    private List<String> recoveryCodes;

    private String userID;

    public BackupKeys(List<String> encryptedRecoveryCodes, String userID) {
        this.recoveryCodes = encryptedRecoveryCodes;
        this.userID = userID;
    }
}
