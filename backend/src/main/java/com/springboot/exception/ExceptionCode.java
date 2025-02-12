package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    QUESTION_EXISTS(409, "Question exists"),
    QUESTION_NOT_FOUND(404, "Question not found"),
    QUESTION_NOT_OWNER(403, "해당 질문의 작성자만 수정할 수 있습니다"),
    QUESTION_ALREADY_ANSWERED(400, "답변 완료된 질문은 수정할 수 없습니다");
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

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
