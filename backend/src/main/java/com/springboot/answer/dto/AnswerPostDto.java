package com.springboot.answer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AnswerPostDto {
    @NotBlank
    private String content;

    private long questionId;

    private long memberId;

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
