package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.question.entity.Question;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class QuestionResponseDto {
    private long questionId;
    private String title;
    private String content;
    private String name;
    private Question.QuestionStatus questionStatus;
    private Question.Visibility visibility;
    private int likeCount;
    private int viewCount;
    private AnswerResponseDto answer;
}
