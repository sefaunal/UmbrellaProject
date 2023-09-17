package com.sefaunal.umbrellachat.Repository;

import com.sefaunal.umbrellachat.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author github.com/sefaunal
 * created on 2023-09-17
 **/

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
