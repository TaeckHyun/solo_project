package com.springboot.answer.entity;

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
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    // 동기화, 영속성 전이
    public void setQuestion(Question question) {
        this.question = question;
        if (question.getAnswer() != this) {
            question.setAnswer(this);
        }
    }
}
