package com.springboot.answer.controller;

import com.springboot.answer.dto.AnswerPatchDto;
import com.springboot.answer.dto.AnswerPostDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/answers")
@Validated
public class AnswerController {
    // 답변 생성
    @PostMapping
    public ResponseEntity postAnswer(@RequestBody @Valid AnswerPostDto answerPostDto) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{answer-id}")
    // 답변 수정
    public ResponseEntity patchAnswer(@PathVariable("answer-id") @Positive long answerId,
                                      @RequestBody @Valid AnswerPatchDto answerPatchDto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 특정 답변 조회
    @GetMapping("/{answer-id}")
    public ResponseEntity getAnswer(@PathVariable("answer-id") @Positive long answerId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 전체 답변 조회
    @GetMapping
    public ResponseEntity getAnswers() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 답변 삭제
    @DeleteMapping("/{answer-id}")
    public ResponseEntity deleteAnswer(@PathVariable("answer-id") @Positive long answerId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
