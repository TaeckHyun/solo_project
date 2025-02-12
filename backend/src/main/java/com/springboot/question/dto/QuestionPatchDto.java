package com.springboot.question.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class QuestionPatchDto {
    private long questionId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
