package com.kogo.kologbackend.domains.emotion.web.adviser;

import com.kogo.kologbackend.domains.emotion.application.exception.DuplicateEmotionException;
import com.kogo.kologbackend.domains.emotion.application.exception.EmotionLogNotFoundException;
import com.kogo.kologbackend.domains.emotion.application.exception.EmotionUserNotFoundException;
import com.kogo.kologbackend.domains.emotion.application.exception.InvalidEmotionException;
import com.kogo.kologbackend.domains.emotion.web.controller.EmotionController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = EmotionController.class)
public class EmotionExceptionAdviser {
    @ExceptionHandler(InvalidEmotionException.class)
    public ProblemDetail invalidEmotion(InvalidEmotionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmotionUserNotFoundException.class)
    public ProblemDetail missingUser(EmotionUserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(EmotionLogNotFoundException.class)
    public ProblemDetail missingLog(EmotionLogNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateEmotionException.class)
    public ProblemDetail duplicateEmotion(DuplicateEmotionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}
