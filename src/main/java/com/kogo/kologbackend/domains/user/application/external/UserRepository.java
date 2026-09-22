package com.kogo.kologbackend.domains.user.application.external;

import com.kogo.kologbackend.domains.user.domain.User;

public interface UserRepository {
    User save(User user);
}
