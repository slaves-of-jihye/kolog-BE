package com.kogo.kologbackend.domains.user.web;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateEmailException;
import com.kogo.kologbackend.domains.user.application.profile.internal.UserProfileGetUseCase;
import com.kogo.kologbackend.domains.user.application.profile.internal.UserProfileUpdateUseCase;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupRequest;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupResponse;
import com.kogo.kologbackend.domains.user.application.usecase.signup.UserSignupUseCase;
import com.kogo.kologbackend.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserSignupUseCase userSignupUseCase;

    @Mock
    private UserProfileGetUseCase userProfileGetUseCase;

    @Mock
    private UserProfileUpdateUseCase userProfileUpdateUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new UserController(userSignupUseCase, userProfileGetUseCase, userProfileUpdateUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void 회원가입에_성공하면_201과_토큰을_반환한다() throws Exception {
        when(userSignupUseCase.signup(any(UserSignupRequest.class)))
                .thenReturn(new UserSignupResponse("access-token", "refresh-token"));

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType("application/json")
                        .content("""
                                {"email":"new@kolog.com","password":"password","nickname":"nick"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void 중복된_이메일이면_409를_반환한다() throws Exception {
        when(userSignupUseCase.signup(any(UserSignupRequest.class)))
                .thenThrow(new DuplicateEmailException("dup@kolog.com"));

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType("application/json")
                        .content("""
                                {"email":"dup@kolog.com","password":"password","nickname":"nick"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
