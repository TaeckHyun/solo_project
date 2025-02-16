package com.springboot.question.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.QUESTION_PUBLIC;

    // Member와 Question은 1 : N 관계
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    // Answer와 Question은 1 : 1 관계
    // 질문이 사라지면 답변도 같이 사라짐
    @OneToOne(mappedBy = "question", cascade = CascadeType.PERSIST)
    private Answer answer;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column
    private String imageUrl;

    // 동기화, 영속성 전이
    public void setAnswer(Answer answer) {
        this.answer = answer;
        if (answer != null) {
            answer.setQuestion(this);
        }
    }

    // 동기화, 영속성 전이
    public void setMember(Member member) {
        this.member = member;
        if (!member.getQuestions().contains(this)) {
            member.setQuestion(this);
        }
    }

    public enum QuestionStatus {
        QUESTION_REGISTERED("질문 등록"),
        QUESTION_ANSWERED("질문 답변 완료"),
        QUESTION_DELETED("질문 삭제"),
        QUESTION_DEACTIVED("질문 비활성화 상태");

        @Getter
        private String status;

        QuestionStatus(String status) {
            this.status = status;
        }
    }

    public enum Visibility {
        QUESTION_PUBLIC("공개글"),
        QUESTION_SECRET("비밀글");

        @Getter
        private String status;

        Visibility(String status) {
            this.status = status;
        }
    }
}
