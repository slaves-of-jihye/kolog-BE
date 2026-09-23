package com.kogo.kologbackend.domains.user.infrastructure;

import com.kogo.kologbackend.domains.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Test
    void 도메인을_엔티티로_변환해_저장하고_다시_도메인으로_반환한다() {
        UserRepositoryImpl userRepository = new UserRepositoryImpl(userJpaRepository);
        User user = new User(null, "new@kolog.com", "password", "nick");
        UserJpaEntity savedEntity = UserJpaEntity.builder()
                .id(1L)
                .email("new@kolog.com")
                .password("password")
                .nickname("nick")
                .build();
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(savedEntity);

        User saved = userRepository.save(user);

        assertThat(saved.id()).isEqualTo(1L);
        assertThat(saved.email()).isEqualTo("new@kolog.com");
    }

    @Test
    void existsByEmail_호출을_그대로_위임한다() {
        UserRepositoryImpl userRepository = new UserRepositoryImpl(userJpaRepository);
        when(userJpaRepository.existsByEmail("dup@kolog.com")).thenReturn(true);

        boolean exists = userRepository.existsByEmail("dup@kolog.com");

        assertThat(exists).isTrue();
        verify(userJpaRepository).existsByEmail("dup@kolog.com");
    }
}
