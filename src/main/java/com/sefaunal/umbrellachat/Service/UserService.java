package com.sefaunal.umbrellachat.Service;

import com.sefaunal.umbrellachat.Model.User;
import com.sefaunal.umbrellachat.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author github.com/sefaunal
 * @since 2023-11-14
 */

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findUserByMail(String userMail) {
        return userRepository.findByEmail(userMail);
    }
}
