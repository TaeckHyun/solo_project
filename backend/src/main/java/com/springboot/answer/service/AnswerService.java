package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    // 답변 생성 서비스 로직 구현
    public Answer createAnswer(Answer answer) {
        return null;
    }

    // 답변 수정 서비스 로직 구현
    public Answer updateAnswer(Answer answer) {
        return null;
    }

    // 특정 답변 조회 서비스 로직 구현
    public Answer findAnswer(long answerId) {
        return null;
    }

    // 전체 답변 조회 서비스 로직 구현
    public Page<Answer> findAnswers() {
        return null;
    }

    // 답변 삭제 서비스 로직 구현
    public void deleteAnswer(long answerId) {
    }
}
