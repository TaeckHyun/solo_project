package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class QuestionResponseDto {
    private long questionId;
    private String title;
    private String content;
    private Question.QuestionStatus questionStatus;
    private Question.Visibility visibility;
    private int likeCount;
}
