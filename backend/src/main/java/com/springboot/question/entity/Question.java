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
    private Status status = Status.QUESTION_REGISTERED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.QUESTION_PUBLIC;

    // Member와 Question은 1 : N 관계
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    // Answer와 Question은 1 : 1 관계
    // 질문이 사라지면 답변도 같이 사라짐
    @OneToOne(mappedBy = "question", cascade = CascadeType.REMOVE)
    private Answer answer;

    @Column(nullable = false)
    private Long likeCount;

    // 동기화, 영속성 전이
    public void setAnswer(Answer answer) {
        this.answer = answer;
        if (answer.getQuestion() != this) {
            answer.setQuestion(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if (!member.getQuestions().contains(this)) {
            member.setQuestion(this);
        }
    }

    public void addCount() {
        this.likeCount = likeCount + 1;
    }

    public enum Status {
        QUESTION_REGISTERED,
        QUESTION_ANSWERED,
        QUESTION_DELETED,
        QUESTION_DEACTIVED
    }

    public enum Visibility {
        QUESTION_PUBLIC,
        QUESTION_SECRET
    }
}
