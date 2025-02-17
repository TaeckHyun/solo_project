package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
public class QuestionPostDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long memberId;

    private Question.Visibility visibility = Question.Visibility.QUESTION_PUBLIC; // 기본값 PUBLIC

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
