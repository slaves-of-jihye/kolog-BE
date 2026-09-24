package com.kogo.kologbackend.domains.user.application.external;

import com.kogo.kologbackend.domains.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
