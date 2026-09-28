package com.kogo.kologbackend.domains.comment.application.exception;

public class CommentUserNotFoundException extends RuntimeException {
    public CommentUserNotFoundException() {
        super("유저를 찾을 수 없습니다.");
    }
}
