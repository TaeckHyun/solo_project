package com.springboot.member.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.MEMBER_ACTIVE;

    // Member와 Question은 1 : N 관계
    @OneToMany(mappedBy = "member")
    private List<Question> questions = new ArrayList<>();

    // 동기화, 영속성 전이
    public void setQuestion(Question question) {
        questions.add(question);
        if (question.getMember() != this) {
            question.setMember(this);
        }
    }

    public enum Status {
        MEMBER_ACTIVE("활동 상태"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        Status(String status) {
            this.status = status;
        }
    }
}
