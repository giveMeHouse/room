package com.sns.room.global.exception;

public class InvalidCommentException extends RuntimeException {

    public InvalidCommentException() {
        super("존재하지 않는 댓글입니다.");
    }
}
