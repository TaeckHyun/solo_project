package com.springboot.answer.controller;

import com.springboot.answer.dto.AnswerPatchDto;
import com.springboot.answer.dto.AnswerPostDto;
import com.springboot.answer.entity.Answer;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.service.AnswerService;
import com.springboot.auth.utils.IdAndEmailPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/questions/{question-id}/answer")
@Validated
public class AnswerController {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;

    public AnswerController(AnswerService answerService, AnswerMapper answerMapper) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
    }

    // 답변 생성
    @PostMapping
    public ResponseEntity postAnswer(@PathVariable("question-id") long questionId,
                                     @RequestBody @Valid AnswerPostDto answerPostDto,
                                     @AuthenticationPrincipal IdAndEmailPrincipal idAndEmailPrincipal) {
        answerPostDto.setQuestionId(questionId);

        answerPostDto.setMemberId(idAndEmailPrincipal.getMemberId());

        Answer answer = answerMapper.answerPostToAnswer(answerPostDto);

        answerService.createAnswer(answer);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{answer-id}")
    // 답변 수정
    public ResponseEntity patchAnswer(@PathVariable("answer-id") @Positive long answerId,
                                      @RequestBody @Valid AnswerPatchDto answerPatchDto) {
        answerPatchDto.setAnswerId(answerId);

        Answer answer = answerMapper.answerPatchToAnswer(answerPatchDto);

        answerService.updateAnswer(answer);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 답변 삭제
    @DeleteMapping("/{answer-id}")
    public ResponseEntity deleteAnswer(@PathVariable("answer-id") @Positive long answerId) {

        answerService.deleteAnswer(answerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
