package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원 생성 서비스 로직 구현
    public Member createMember(Member member) {
        return null;
    }

    // 회원 정보 수정 서비스 로직 구현
    public Member updateMember(Member member) {
        return null;
    }

    // 특정 회원 정보 찾는 서비스 로직 구현
    public Member findMember(long memberId) {
        return null;
    }

    // 회원 전체 정보 조회 서비스 로직 구현
    public List<Member> findMembers() {
        return null;
    }

    // 회원 삭제 서비스 로직 구현
    public void deleteMember() {

    }

    // 가입이 되어있는 회원인지를 검증
    public void verifyExistsEmail(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }
}
