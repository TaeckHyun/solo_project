package com.springboot.question.controller;

import com.springboot.auth.utils.IdAndEmailPrincipal;
import com.springboot.auth.utils.MemberDetailsService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.question.dto.QuestionPatchDto;
import com.springboot.question.dto.QuestionPostDto;
import com.springboot.question.dto.QuestionResponseDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.question.service.QuestionService;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/questions")
@Validated
public class QuestionController {
    private final static String QUESTION_DEFAULT_URL = "/v1/questions";
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;
    private final MemberDetailsService memberDetailsService;

    public QuestionController(QuestionService questionService, QuestionMapper questionMapper,
                              QuestionRepository questionRepository,
                              MemberDetailsService memberDetailsService) {
        this.questionService = questionService;
        this.questionMapper = questionMapper;
        this.questionRepository = questionRepository;
        this.memberDetailsService = memberDetailsService;
    }

    // 질문 생성
    @PostMapping
    public ResponseEntity postQuestion(@RequestBody QuestionPostDto questionPostDto,
                                       @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        // 질문을 생성하기 전에 로그인한 특정 사용자가 질문을 생성하는 거니까 questionPostDto 안에
        // 검증된 ID 넣어줘야됨
        questionPostDto.setMemberId(idAndEmailPrincipal.getMemberId());

        Question question = questionMapper.questionPostDtoToQuestion(questionPostDto);

        Question createQuestion = questionService.createQuestion(question);

        URI location = UriCreator.createUri(QUESTION_DEFAULT_URL, createQuestion.getQuestionId());

        return ResponseEntity.created(location).build();
    }

    // 질문 수정
    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(@PathVariable("question-id") @Positive long questionId,
                                        @Valid @RequestBody QuestionPatchDto questionPatchDto,
                                        @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        questionPatchDto.setQuestionId(questionId);
        questionPatchDto.setMemberId(idAndEmailPrincipal.getMemberId());

        Question question = questionMapper.questionPatchDtoToQuestion(questionPatchDto);

        Question newQuestion =
                questionService.updateQuestion(question, questionPatchDto.getMemberId());

        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(newQuestion);

        return new ResponseEntity<>(questionResponseDto, HttpStatus.OK);
    }

    // 특정 질문 조회
    @GetMapping("/{question-id}")
    public ResponseEntity getQuestion(@PathVariable("question-id") @Positive long questionId,
                                      @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        Question question = questionService.findQuestion(questionId,idAndEmailPrincipal.getMemberId());

        QuestionResponseDto questionResponseDto = questionMapper.questionToQuestionResponseDto(question);

        return new ResponseEntity<>(new SingleResponseDto<>(questionResponseDto), HttpStatus.OK);
    }

    // 질문 전체 조회
    @GetMapping
    public ResponseEntity getQuestions(@RequestParam int page,
                                       @RequestParam int size) {

        Page<Question> questionPage = questionService.findQuestions(page, size);
        List<Question> questions = questionPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(questionMapper.questionsToQuestionResponseDtos(questions), questionPage), HttpStatus.OK);
    }

    // 질문 삭제
    @DeleteMapping("/{question-id}")
    public ResponseEntity deleteQuestion(@PathVariable("question-id") @Positive long questionId,
                                         @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        questionService.deleteQuestion(questionId, idAndEmailPrincipal.getMemberId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
