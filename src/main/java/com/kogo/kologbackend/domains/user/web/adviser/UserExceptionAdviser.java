package com.kogo.kologbackend.domains.user.web.adviser;

import com.kogo.kologbackend.domains.user.application.exception.DuplicateEmailException;
import com.kogo.kologbackend.domains.user.application.exception.DuplicateNicknameException;
import com.kogo.kologbackend.domains.user.application.exception.InvalidCredentialsException;
import com.kogo.kologbackend.domains.user.application.exception.InvalidProfileImageException;
import com.kogo.kologbackend.domains.user.application.exception.InvalidProfileUpdateException;
import com.kogo.kologbackend.domains.user.application.exception.InvalidRefreshTokenException;
import com.kogo.kologbackend.domains.user.application.exception.ProfileImageUploadException;
import com.kogo.kologbackend.domains.user.application.exception.UserNotFoundException;
import com.kogo.kologbackend.domains.user.web.controller.UserController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionAdviser {
    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail duplicateEmail(DuplicateEmailException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail invalidCredentials(InvalidCredentialsException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ProblemDetail invalidRefreshToken(InvalidRefreshTokenException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail userNotFound(UserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ProblemDetail duplicateNickname(DuplicateNicknameException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidProfileUpdateException.class)
    public ProblemDetail invalidProfileUpdate(InvalidProfileUpdateException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidProfileImageException.class)
    public ProblemDetail invalidProfileImage(InvalidProfileImageException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ProfileImageUploadException.class)
    public ProblemDetail profileImageUploadFailed(ProfileImageUploadException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
