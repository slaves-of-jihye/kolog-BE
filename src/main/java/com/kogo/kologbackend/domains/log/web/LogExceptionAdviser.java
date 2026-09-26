package com.kogo.kologbackend.domains.log.web;

import com.kogo.kologbackend.domains.log.application.exception.InvalidLogDateException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.exception.LogUserNotFoundException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = LogController.class)
public class LogExceptionAdviser {
    @ExceptionHandler(InvalidVideoException.class)
    public ProblemDetail invalidVideo(InvalidVideoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidLogDateException.class)
    public ProblemDetail invalidDate(InvalidLogDateException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(LogUserNotFoundException.class)
    public ProblemDetail missingUser(LogUserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(VideoUploadException.class)
    public ProblemDetail uploadFailed(VideoUploadException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
