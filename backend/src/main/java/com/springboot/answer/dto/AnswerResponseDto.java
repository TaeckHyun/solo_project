package com.springboot.answer.dto;

import com.springboot.question.entity.Question;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnswerResponseDto {
    private long answerId;
    private String content;
}
