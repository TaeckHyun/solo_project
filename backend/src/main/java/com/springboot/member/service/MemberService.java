package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        verifyExistsEmail(member.getEmail());

        return memberRepository.save(member);
    }

    // 회원 정보 수정 서비스 로직 구현
    public Member updateMember(Member member) {
        Member findMember = findVerifiedMember(member.getMemberId());

        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getEmail())
                .ifPresent(email -> findMember.setEmail(email));
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getStatus())
                .ifPresent(status -> findMember.setStatus(status));

        return memberRepository.save(findMember);
    }

    // 특정 회원 정보 찾는 서비스 로직 구현
    public Member findMember(long memberId) {
        return findVerifiedMember(memberId);
    }

    // 회원 전체 정보 조회 서비스 로직 구현
    public Page<Member> findMembers(int page, int size) {
        return memberRepository.findAll(PageRequest.of(page, size, Sort.by("memberId").descending()));
    }

    // 회원 삭제 서비스 로직 구현
    public void deleteMember(long memberId) {
        Member findMember = findVerifiedMember(memberId);

        memberRepository.delete(findMember);
    }

    // 가입이 되어있는 회원인지를 검증
    public void verifyExistsEmail(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    // 회원이 DB에 존재하는지 검증 후 그 회원을 반환하는 메서드
    public Member findVerifiedMember(long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        return findMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
