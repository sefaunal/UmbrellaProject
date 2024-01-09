package com.sefaunal.umbrellaauth.Repository;

import com.sefaunal.umbrellaauth.Model.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author github.com/sefaunal
 * @since 2023-12-04
 */
@Repository
public interface LoginHistoryRepository extends MongoRepository<LoginHistory, String> {
    Page<LoginHistory> getLoginRecordsByUserID(String userID, Pageable pageable);
}
