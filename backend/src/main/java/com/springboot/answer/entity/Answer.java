package com.springboot.answer.entity;

import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String content;

    // Answer와 Question은 1 : 1 관계
    @OneToOne
    @JoinColumn(name = "QUESTION_ID", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    private AnswerStatus answerStatus = AnswerStatus.ANSWER_PUBLIC;

    public enum AnswerStatus {
        ANSWER_PUBLIC("공개 답변"),
        ANSWER_SECRET("비밀 답변");

        @Getter
        private String status;

        AnswerStatus(String status) {
            this.status = status;
        }
    }

    // 동기화, 영속성 전이
    public void setQuestion(Question question) {
        this.question = question;
        if (question.getAnswer() != this) {
            question.setAnswer(this);
        }
    }
}
