package com.kogo.kologbackend.domains.user.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void 저장된_이메일이면_존재한다고_응답한다() {
        userJpaRepository.save(UserJpaEntity.builder()
                .email("exists@kolog.com")
                .password("password")
                .nickname("nick")
                .build());

        assertThat(userJpaRepository.existsByEmail("exists@kolog.com")).isTrue();
    }

    @Test
    void 저장되지_않은_이메일이면_존재하지_않는다고_응답한다() {
        assertThat(userJpaRepository.existsByEmail("none@kolog.com")).isFalse();
    }

    @Test
    void 저장된_닉네임이면_존재한다고_응답한다() {
        userJpaRepository.save(UserJpaEntity.builder()
                .email("nick-owner@kolog.com")
                .password("password")
                .nickname("duplicate-nick")
                .build());

        assertThat(userJpaRepository.existsByNickname("duplicate-nick")).isTrue();
        assertThat(userJpaRepository.existsByNickname("unused-nick")).isFalse();
    }
}
