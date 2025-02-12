package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public QuestionService(QuestionRepository questionRepository,
                           MemberRepository memberRepository,
                           MemberService memberService) {
        this.questionRepository = questionRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    // 질문 생성 서비스 로직 구현
    public Question createQuestion(Question question) {
        memberService.verifyExistsEmail(question.getMember().getEmail());
        return questionRepository.save(question);
    }

    // 질문 수정 서비스 로직 구현
    public Question updateQuestion(Question question) {
        // 질문을 수정하기 전에 내가 올린 질문이 존재하는지 확인
        Question findquestion = findVerifiedQuestion(question.getQuestionId());

        Optional.ofNullable(question.getTitle())
                .ifPresent(title -> findquestion.setTitle(title));
        Optional.ofNullable(question.getContent())
                .ifPresent(content -> findquestion.setContent(content));
        Optional.ofNullable(question.getStatus())
                .ifPresent(questionStatus -> findquestion.setStatus(questionStatus));
        Optional.ofNullable(question.getVisibility())
                .ifPresent(visibility -> findquestion.setVisibility(visibility));

        return questionRepository.save(findquestion);
    }

    // 특정 질문 조회 서비스 로직 구현
    public Question findQuestion(long questionId) {
        return findVerifiedQuestion(questionId);
    }

    // 전체 질문 조회 서비스 로직 구현
    public Page<Question> findQuestions(int page, int size) {
        return questionRepository.findAll(PageRequest.of(page-1, size,
                Sort.by("questionId").descending()));
    }

    // 질문 삭제 서비스 로직 구현
    public void deleteQuestion(long questionId) {
        questionRepository.delete(findVerifiedQuestion(questionId));
    }

    // 질문이 DB에 존재하는지 검증 후 가져온 질문을 반환하는 메서드
    public Question findVerifiedQuestion(long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }
}
