package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
public class QuestionPatchDto {
    private long questionId;

    private long memberId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Question.Visibility visibility;

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
