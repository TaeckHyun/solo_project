package com.springboot.like.repository;

import com.springboot.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberAndQuestion(long memberId, long questionId);
    // 특정 질문에 대해 특정 회원이 좋아요를 눌렀는지 확인
    Optional<Like> findByQuestion_QuestionIdAndMember_MemberId(long questionId, long memberId);
}
