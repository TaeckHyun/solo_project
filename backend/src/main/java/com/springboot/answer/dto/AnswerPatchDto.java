package com.springboot.answer.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class AnswerPatchDto {
    private long answerId;

    @NotBlank
    private String content;

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }
}
