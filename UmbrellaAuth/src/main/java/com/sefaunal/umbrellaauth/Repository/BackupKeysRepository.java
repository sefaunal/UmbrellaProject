package com.sefaunal.umbrellaauth.Repository;

import com.sefaunal.umbrellaauth.Model.BackupKeys;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author github.com/sefaunal
 * @since 2023-12-18
 */
public interface BackupKeysRepository extends MongoRepository<BackupKeys, String> {
    BackupKeys findByUserID(String userID);
}
