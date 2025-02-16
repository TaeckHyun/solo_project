package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.question.service.QuestionService;
import com.springboot.utils.CheckValidator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AnswerService {
    private final MemberService memberService;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;
    private final CheckValidator checkValidator;
    private final QuestionRepository questionRepository;

    public AnswerService(MemberService memberService, QuestionService questionService, AnswerRepository answerRepository, CheckValidator checkValidator, QuestionRepository questionRepository) {
        this.memberService = memberService;
        this.questionService = questionService;
        this.answerRepository = answerRepository;
        this.checkValidator = checkValidator;
        this.questionRepository = questionRepository;
    }

    // 답변 생성 서비스 로직 구현
    public Answer createAnswer(Answer answer) {
        // 먼저 회원이 있는지 검증해야함
        memberService.findVerifiedMember(answer.getMember().getMemberId());

        // 여기서 그 회원이 관리자 인지 검증을 해야함
        checkValidator.checkAdmin();

        // 질문에 답변이 있는지 확인해야함
        Question question = verifyAnswerOfQuestion(answer);

        // 질문이 비밀글이라면 답변도 비밀답변이여야함 상태 변경 필수
        if (question.getVisibility() == Question.Visibility.QUESTION_SECRET) {
            answer.setAnswerStatus(Answer.AnswerStatus.ANSWER_SECRET);
        }

        // 답변이 등록이 되면 질문의 상태 값이 QUESTION_ANSWERED 로 바뀌어야함
        question.setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);

        // 질문 상태 변경 사항 저장
        questionRepository.save(question);

        // 답변 저장
        return answerRepository.save(answer);
    }

    // 답변 수정 서비스 로직 구현
    public Answer updateAnswer(Answer answer) {
        // 관리자 검증
        checkValidator.checkAdmin();

        // 답변이 일단 달려있어야해서 답변 찾아야함
        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());

        // 변경 내용 수정
        Optional.ofNullable(answer.getContent())
                .ifPresent(content -> findAnswer.setContent(content));

        return answerRepository.save(findAnswer);
    }

    // 답변 삭제 서비스 로직 구현
    @Transactional
    public void deleteAnswer(long answerId) {
        // 관리자 검증 해야함
        checkValidator.checkAdmin();
        // 먼저 답변이 존재하는지 검증 해야함
        Answer answer = findVerifiedAnswer(answerId);
        // Null로 바꿔야하나? 그건 모르겠음
        questionService.setAnswerOfQuestion(answer.getQuestion().getQuestionId());
        answerRepository.deleteById(answerId);
    }

    // 답변이 존재하는지 검증하는 메서드 필요
    public Answer findVerifiedAnswer(long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        return answer.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
    }

    // 질문에 대한 답변이 달려있는지 검증하는 메서드 필요
    public Question verifyAnswerOfQuestion(Answer answer) {
        Question question = questionService.findVerifiedQuestion(answer.getQuestion().getQuestionId());
        // 답변이 이미 달려있는 상태라면? 예외 던져야함
        if (question.getAnswer() != null) {
            throw new BusinessLogicException(ExceptionCode.ANSWER_EXISTS);
        }
        return question;
    }
}
