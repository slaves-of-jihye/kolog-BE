package com.kogo.kologbackend.adapter.auth.Service;

import com.kogo.kologbackend.adapter.auth.dto.request.SignupRequest;
import com.kogo.kologbackend.adapter.auth.dto.response.AuthResponse;
import com.kogo.kologbackend.adapter.auth.provider.JwtProvider;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse.UserDto signup(SignupRequest signupRequest) {
        boolean emailExist = userRepository.existsByEmail(signupRequest.email());
        if (emailExist) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(signupRequest.email())
                .password(passwordEncoder.encode(signupRequest.password()))
                .build();

        User saveUser = userRepository.save(user);
        return new AuthResponse.UserDto(
                saveUser.getId(),
                saveUser.getEmail()
        );
    }
}
