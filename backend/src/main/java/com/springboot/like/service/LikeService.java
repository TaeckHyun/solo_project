package com.springboot.like.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.entity.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.question.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LikeService {
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final MemberService memberService;
    private final LikeRepository likeRepository;

    public LikeService(QuestionService questionService,
                       QuestionRepository questionRepository,
                       MemberService memberService,
                       LikeRepository likeRepository) {
        this.questionService = questionService;
        this.questionRepository = questionRepository;
        this.memberService = memberService;
        this.likeRepository = likeRepository;
    }


    public void addOrMinusLike(long questionId, long principalId) {
        Optional<Like> likes = likeRepository.findByQuestion_QuestionIdAndMember_MemberId(questionId, principalId);

        // 좋아요를 누른다는 건 먼저 좋아요를 누를 게시글이 존재해야하는지도 따져야 함
        Question question = questionService.findVerifiedQuestion(questionId);

        // 좋아요를 눌렀다면 좋아요 객체가 있을것이고 좋아요를 누르지 않았다면 empty 일거임
        if (likes.isPresent()) {
            likeRepository.delete(likes.get());
            question.setLikeCount(question.getLikeCount() - 1);
        } else {
            // 좋아요를 눌렀을 때 Like 객체 생성
            Like like = new Like();
            like.setMember(memberService.findVerifiedMember(principalId));
            like.setQuestion(question);
            likeRepository.save(like);
            question.setLikeCount(question.getLikeCount() + 1);
        }
    }

//    // 좋아요를 추가하는 메서드
//    public Like addLike(long memberId, long questionId) {
//        // 좋아요를 누른다는 건 먼저 가입 된 회원인지 검증 해야 하지 않나?
//        Member member = memberService.findVerifiedMember(memberId);
//
//        // 좋아요를 누른다는 건 먼저 좋아요를 누를 게시글이 존재해야하는지도 따져야 함
//        Question question = questionService.findVerifiedQuestion(questionId);
//
//        // 좋아요 중복 방지
//        alreadyLiked(member.getMemberId(), question.getQuestionId());
//
//        // 좋아요를 눌렀을 때 Like 객체 생성
//        Like like = new Like();
//        like.setMember(member);
//        like.setQuestion(question);
//        likeRepository.save(like);
//
//        // 좋아요가 생겼다는 건 질문글의 좋아요 숫자가 올라가야함
//        questionService.addLikeCount(question);
//        questionRepository.save(question);
//
//        return like;
//    }
//
//    // 좋아요를 삭제하는 메서드
//    public void removeLike(long memberId, long questionId) {
//        // 가입 된 회원 데이터 가져옴
//        Member member = memberService.findVerifiedMember(memberId);
//
//        // 질문 글 등록되어있는지 확인하고 가져옴
//        Question question = questionService.findVerifiedQuestion(questionId);
//
//        // 좋아요를 취소했는데 또 취소 방지
//        removeLiked(member.getMemberId(), question.getQuestionId());
//
//        // 좋아요 레포에서 특정 회원이 특정 질문에 남긴 좋아요 객체를 찾아옴
//        Optional<Like> findLike = likeRepository.findByMemberAndQuestion(member.getMemberId(), question.getQuestionId());
//
//        // 찾아온 데이터 NULL 값 검증
//        Like like = findLike.orElseThrow(() ->
//            new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND));
//
//        // 좋아요 취소니까 좋아요 레포에서 특정 회원이 특정 질문에 남긴 좋아요 객체 없애야됨
//        likeRepository.delete(like);
//
//        // 좋아요가 취소됐다는 건 질문글의 좋아요 숫자가 내려가야함
//        questionService.removeLikeCount(question);
//        questionRepository.save(question);
//    }

    // 좋아요를 이미 눌렀는데 또 누른다면? 중복으로 추가 되는걸 방지해야함
//    public void alreadyLiked(long memberId, long questionId) {
//        boolean exists = likeRepository.existsByMemberAndQuestion(memberId, questionId);
//        if (exists) {
//            throw new BusinessLogicException(ExceptionCode.ALREADY_LIKE);
//        }
//    }
//
//    // 이미 취소된 좋아요를 다시 취소하는건 말이안됨 방지해야함
//    public void removeLiked(long memberId, long questionId) {
//        boolean exists = likeRepository.existsByMemberAndQuestion(memberId, questionId);
//        // 좋아요가 존재하는지 먼저 확인 false면
//        if (!exists) {
//            throw new BusinessLogicException(ExceptionCode.ALREADY_LIKE);
//        }
//    }
}
