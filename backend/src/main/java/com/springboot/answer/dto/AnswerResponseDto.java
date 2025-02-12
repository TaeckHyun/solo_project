package com.springboot.answer.dto;

import com.springboot.question.entity.Question;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnswerResponseDto {
    private long answerId;
    private long questionId;
    private String content;
    // 답변에 대한 ResponseDto를 받았을때 Question 객체 자체를 받아야할까?
    // 아니면 어디 question에 달렸는지 ID만 있다면 노상관?
    // 이건 고민해봐야 할 문제
    // private Question question;
}
