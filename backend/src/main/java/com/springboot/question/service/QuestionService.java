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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    public Question updateQuestion(Question question, long memberId) {
        // 질문을 수정하기 전에 내가 올린 질문이 존재하는지 확인
        Question findquestion = findVerifiedQuestion(question.getQuestionId());

        // 일단 질문을 수정하려면 내가 쓴 글만 수정이되어야함 즉 작성자가 맞는지를 따져야 하는거 아닌가?
        if(question.getMember().getMemberId() != memberId) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_OWNER);
        }

        // 답변이 이미 달려서 질문 답변 완료 상태인데 이걸 수정할 수는 없음
        if(findquestion.getQuestionStatus() == Question.QuestionStatus.QUESTION_ANSWERED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_ALREADY_ANSWERED);
        }

        Optional.ofNullable(question.getTitle())
                .ifPresent(title -> findquestion.setTitle(title));
        Optional.ofNullable(question.getContent())
                .ifPresent(content -> findquestion.setContent(content));
        Optional.ofNullable(question.getVisibility())
                .ifPresent(visibility -> findquestion.setVisibility(visibility));

        return questionRepository.save(findquestion);
    }

    // 특정 질문 조회 서비스 로직 구현
    // 인증
    public Question findQuestion(long questionId) {
        // Authentication을 통해서 Controller에서 인증을 받고 여기로 넘겨줘야함
        // 그러고 권한 없으면 예외처리 던져야됨

        Question question = findVerifiedQuestion(questionId);

        return findVerifiedQuestion(questionId);
    }

    // 전체 질문 조회 서비스 로직 구현
    public Page<Question> findQuestions(int page, int size) {
        return questionRepository.findByQuestionStatusNotIn(Arrays.asList(
                Question.QuestionStatus.QUESTION_DELETED,
                Question.QuestionStatus.QUESTION_DEACTIVED
        ), PageRequest.of(page,size, Sort.by("questionId").descending()));
        // 비밀글인 상태 SECRET 이여도 가져오긴해야됨 보이긴해야지, 비밀글입니다 로 보여야지
    }

    // 질문 삭제 서비스 로직 구현
    public void deleteQuestion(long questionId) {
        Question question = findVerifiedQuestion(questionId);
        question.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);

        questionRepository.save(question);
    }

    // 질문이 DB에 존재하는지 검증 후 가져온 질문을 반환하는 메서드
    public Question findVerifiedQuestion(long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }

    // 질문이 삭제 상태인지를 검증하는 메서드 필요
    public void verifyQuestionDeleteStatus(Question question) {
        if(question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }
    }

    // 좋아요 숫자가 증가하는 메서드 필요
    public void addLikeCount(Question question) {
        question.setLikeCount(question.getLikeCount() + 1);
        questionRepository.save(question);
    }

    // 좋아요 숫자가 감소하는 메서드 필요
    public void removeLikeCount(Question question) {
        question.setLikeCount(question.getLikeCount() - 1);
        questionRepository.save(question);
    }
}
