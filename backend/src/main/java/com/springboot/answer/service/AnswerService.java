package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import com.springboot.utils.CheckValidator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerService {
    private final MemberService memberService;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;
    private final CheckValidator checkValidator;

    public AnswerService(MemberService memberService, QuestionService questionService, AnswerRepository answerRepository, CheckValidator checkValidator) {
        this.memberService = memberService;
        this.questionService = questionService;
        this.answerRepository = answerRepository;
        this.checkValidator = checkValidator;
    }

    // 답변 생성 서비스 로직 구현
    public Answer createAnswer(Answer answer) {
        // 먼저 회원이 있는지 검증해야함
        memberService.findVerifiedMember(answer.getMember().getMemberId());

        // 여기서 그 회원이 관리자 인지 검증을 해야함
        checkValidator.checkAdmin();

        // 답변이 이미 달렸는지 검증 해야함
        return null;
    }

    // 답변 수정 서비스 로직 구현
    public Answer updateAnswer(Answer answer) {
        return null;
    }

    // 답변 삭제 서비스 로직 구현
    public void deleteAnswer(long answerId) {
    }

    // 질문에 대한 답변이 존재하는지 검증하는 메서드 필요
    public Answer findVerifiedAnswer(long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);

        return answer.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
    }
}
