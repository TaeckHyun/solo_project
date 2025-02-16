package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    MEMBER_NOT_OWNER(403, "You are not the owner of this resource"),
    QUESTION_NOT_FOUND(404, "Question not found"),
    QUESTION_NOT_OWNER(403, "해당 질문의 작성자만 수정할 수 있습니다"),
    QUESTION_ALREADY_ANSWERED(400, "답변 완료된 질문은 수정할 수 없습니다"),
    LIKE_NOT_FOUND(404, "좋아요를 찾을 수 없습니다."),
    ALREADY_LIKE(409, "이미 좋아요를 눌렀음"),
    ANSWER_NOT_FOUND(404, "답변을 찾을 수 없습니다."),
    ANSWER_EXISTS(409, "Answer exists"),
    UNAUTHORIZED_ACCESS(403, "관리자 권한이 없습니다.");
//    COFFEE_NOT_FOUND(404, "Coffee not found"),
//    COFFEE_CODE_EXISTS(409, "Coffee Code exists"),
//    ORDER_NOT_FOUND(404, "Order not found"),
//    CANNOT_CHANGE_ORDER(403, "Order can not change"),
//    NOT_IMPLEMENTATION(501, "Not Implementation"),
//    INVALID_MEMBER_STATUS(400, "Invalid member status");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
