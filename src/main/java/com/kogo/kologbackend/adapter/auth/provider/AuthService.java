package com.kogo.kologbackend.adapter.auth.provider;

import com.kogo.kologbackend.adapter.auth.dto.response.AuthResponse;
import com.kogo.kologbackend.application.user.external.UserRepository;
import com.kogo.kologbackend.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public AuthService(GoogleOAuthProvider googleOAuthProvider,
                       JwtProvider jwtProvider,
                       UserRepository userRepository) {
        this.googleOAuthProvider = googleOAuthProvider;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse googleLogin(String code, String redirectUri) {
        // 1. 구글 인가 코드로 구글 유저 이메일 가져오기
        String googleAccessToken = googleOAuthProvider.getAccessToken(code, redirectUri);
        String email = googleOAuthProvider.getUserEmail(googleAccessToken);

        // 2. DB에서 유저 조회 (없으면 신규 가입)
        Optional<User> userOptional = userRepository.findByEmail(email);
        boolean isNewUser = userOptional.isEmpty();

        User user = userOptional.orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .build())
        );

        // 3. 우리 서비스 전용 JWT 토큰(Access, Refresh) 생성
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        // 4. 응답 객체 생성 및 반환
        return new AuthResponse(
                accessToken,
                refreshToken,
                isNewUser,
                new AuthResponse.UserDto(user.getId(), user.getEmail())
        );
    }
}
