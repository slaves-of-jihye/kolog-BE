package com.kogo.kologbackend.domains.comment.web.adviser;

import com.kogo.kologbackend.domains.comment.application.exception.CommentLogNotFoundException;
import com.kogo.kologbackend.domains.comment.application.exception.CommentUserNotFoundException;
import com.kogo.kologbackend.domains.comment.application.exception.InvalidCommentContentException;
import com.kogo.kologbackend.domains.comment.web.controller.CommentController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CommentController.class)
public class CommentExceptionAdviser {
    @ExceptionHandler(InvalidCommentContentException.class)
    public ProblemDetail invalidContent(InvalidCommentContentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CommentUserNotFoundException.class)
    public ProblemDetail missingUser(CommentUserNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CommentLogNotFoundException.class)
    public ProblemDetail missingLog(CommentLogNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
