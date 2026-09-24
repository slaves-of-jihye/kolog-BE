package com.kogo.kologbackend.domains.user.infrastructure;

import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository repository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserMapper.toEntity(user);
        return UserMapper.toDomain(repository.save(entity));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserMapper::toDomain);
    }
}
