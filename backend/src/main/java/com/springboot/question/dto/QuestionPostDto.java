package com.springboot.question.dto;

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

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
