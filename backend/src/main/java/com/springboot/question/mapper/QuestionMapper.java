package com.springboot.question.mapper;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "member.memberId", source = "memberId")
    Question questionPostDtoToQuestion(QuestionPostDto questionPostDto);
    @Mapping(target = "member.memberId", source = "memberId")
    Question questionPatchDtoToQuestion(QuestionPatchDto questionPatchDto);

    default QuestionResponseDto questionToQuestionResponseDto(Question question) {
        QuestionResponseDto.QuestionResponseDtoBuilder builder = QuestionResponseDto.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .content(question.getContent())
                .questionImage(question.getQuestionImage())
                .questionStatus(question.getQuestionStatus())
                .visibility(question.getVisibility())
                .likeCount(question.getLikeCount())
                .viewCount(question.getViewCount())
                .name(question.getMember().getName());

        // 답변이 있는 경우에만 answerResponseDto 설정
        if (question.getAnswer() != null) {
            builder.answer(
                    AnswerResponseDto.builder()
                            .answerId(question.getAnswer().getAnswerId())
                            .content(question.getAnswer().getContent())
                            .build()
            );
        }

        // 최종적으로 build() 호출
        return builder.build();
    }

    List<QuestionResponseDto> questionsToQuestionResponseDtos(List<Question> questions);
}
