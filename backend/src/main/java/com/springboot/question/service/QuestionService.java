package com.springboot.question.service;

import com.springboot.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    // 질문 생성 서비스 로직 구현
    public Question createQuestion(Question question) {
        return null;
    }

    // 질문 수정 서비스 로직 구현
    public Question updateQuestion(Question question) {
        return null;
    }

    // 특정 질문 조회 서비스 로직 구현
    public Question getQuestion(long questionId) {
        return null;
    }

    // 전체 질문 조회 서비스 로직 구현
    public Page<Question> getQuestions(int page, int size) {
        return null;
    }

    // 질문 삭제 서비스 로직 구현
    public void deleteQuestion(long questionId) {
    }
}
