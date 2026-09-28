package com.kogo.kologbackend.domains.comment.application.exception;

public class CommentLogNotFoundException extends RuntimeException {
    public CommentLogNotFoundException() {
        super("로그를 찾을 수 없습니다.");
    }
}
