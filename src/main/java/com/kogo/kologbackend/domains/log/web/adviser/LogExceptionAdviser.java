package com.kogo.kologbackend.domains.log.web.adviser;

import com.kogo.kologbackend.domains.log.application.exception.InvalidLogDateException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidLogHourException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidLogQueryException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidLogUpdateException;
import com.kogo.kologbackend.domains.log.application.exception.InvalidVideoException;
import com.kogo.kologbackend.domains.log.application.exception.LogNotFoundException;
import com.kogo.kologbackend.domains.log.application.exception.LogUpdateForbiddenException;
import com.kogo.kologbackend.domains.log.application.exception.LogUserNotFoundException;
import com.kogo.kologbackend.domains.log.application.exception.VideoUploadException;
import com.kogo.kologbackend.domains.log.web.controller.LogController;
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

    @ExceptionHandler(InvalidLogHourException.class)
    public ProblemDetail invalidHour(InvalidLogHourException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidLogQueryException.class)
    public ProblemDetail invalidQuery(InvalidLogQueryException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidLogUpdateException.class)
    public ProblemDetail invalidUpdate(InvalidLogUpdateException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(LogUserNotFoundException.class)
    public ProblemDetail missingUser(LogUserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(LogNotFoundException.class)
    public ProblemDetail logNotFound(LogNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(LogUpdateForbiddenException.class)
    public ProblemDetail updateForbidden(LogUpdateForbiddenException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(VideoUploadException.class)
    public ProblemDetail uploadFailed(VideoUploadException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
