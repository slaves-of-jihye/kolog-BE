package com.kogo.kologbackend.domains.user.infrastructure.adapter;

import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.domain.User;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaEntity;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserJpaRepository;
import com.kogo.kologbackend.domains.user.infrastructure.jpa.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
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
    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserMapper::toDomain);
    }

}
