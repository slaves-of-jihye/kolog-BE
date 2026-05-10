package com.kogo.kologbackend.adapter.auth.Service;

import com.kogo.kologbackend.adapter.auth.dto.request.LoginRequest;
import com.kogo.kologbackend.adapter.auth.dto.request.SignupRequest;
import com.kogo.kologbackend.adapter.auth.dto.response.AuthResponse;
import com.kogo.kologbackend.adapter.auth.dto.response.LoginResponse;
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
                .nickname(signupRequest.nickname())
                .build();

        User saveUser = userRepository.save(user);
        return new AuthResponse.UserDto(
                saveUser.getId(),
                saveUser.getEmail()
        );
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return new LoginResponse("Bearer", accessToken, refreshToken);
    }
}
