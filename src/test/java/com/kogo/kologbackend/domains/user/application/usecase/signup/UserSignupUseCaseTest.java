package com.kogo.kologbackend.domains.user.application.usecase.signup;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateEmailException;
import com.kogo.kologbackend.domains.user.application.external.UserRepository;
import com.kogo.kologbackend.domains.user.application.usecase.JwtProvider;
import com.kogo.kologbackend.domains.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignupUseCaseTest {

    @Mock
    private UserRepository repository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserSignupUseCase useCase;

    @Test
    void 이미_가입된_이메일이면_예외를_던진다() {
        UserSignupRequest request = new UserSignupRequest("dup@kolog.com", "password", "nick");
        when(repository.existsByEmail("dup@kolog.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.signup(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void 새로운_이메일이면_회원가입에_성공한다() {
        UserSignupRequest request = new UserSignupRequest("new@kolog.com", "password", "nick");
        when(repository.existsByEmail("new@kolog.com")).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(new User(1L, "new@kolog.com", "password", "nick"));
        when(jwtProvider.createAccessToken(1L)).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(1L)).thenReturn("refresh-token");

        UserSignupResponse response = useCase.signup(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }
}
