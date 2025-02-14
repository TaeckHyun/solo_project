package com.springboot.member.service;

import com.springboot.auth.utils.AuthorityUtils;
import com.springboot.auth.utils.IdAndEmailPrincipal;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.helper.event.MemberRegistrationApplicationEvent;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.utils.CheckValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    // Spring 이벤트를 발생시키는 역할
    // 특정 동작(Ex. 유저 생성)이 발생했을 때, 이를 다른 컴포넌트에 비동기적으로 알릴 수 있음
    private final ApplicationEventPublisher publisher;
    // 비밀번호를 해시로 변환하여 안전하게 저장하기 위해 사용
    // 평문을 그대로 저장하면 안되기 때문에 인코딩 하기 위해 불러온거임
    private final PasswordEncoder passwordEncoder;
    private final AuthorityUtils authorityUtils;
    private final CheckValidator checkValidator;

    public MemberService(MemberRepository memberRepository,
                         ApplicationEventPublisher publisher,
                         PasswordEncoder passwordEncoder,
                         AuthorityUtils authorityUtils,
                         CheckValidator checkValidator) {
        this.memberRepository = memberRepository;
        this.publisher = publisher;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
        this.checkValidator = checkValidator;
    }

    // 회원 생성 서비스 로직 구현
    public Member createMember(Member member) {
        // 아이디가 이미 만들어진 상태인지 검증
        verifyExistsEmail(member.getEmail());

        // 전달받은 member 객체에 비밀번호를 가져와서 인코딩 후 할당
        String encryptedPassword = passwordEncoder.encode(member.getPassword());

        // 인코딩 된 비밀번호를 member의 비밀번호로 저장
        member.setPassword(encryptedPassword);

        // 사용자 이메일에 따라서 권한 지정 후 부여
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        Member saveMember = memberRepository.save(member);

        publisher.publishEvent(new MemberRegistrationApplicationEvent(this, saveMember));

        return saveMember;
    }

    // 회원 정보 수정 서비스 로직 구현
    public Member updateMember(Member member, long principalMemberId) {
        // 회원이 존재하는지 먼저 봐야함
        Member findMember = findVerifiedMember(member.getMemberId());

        // 지금 로그인한 회원의 이메일이랑 수정하려고 하는 회원의 이메일이 일치 하는지 확인하는 메서드 필요
        checkValidator.checkOwner(member.getMemberId(), principalMemberId);

        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getEmail())
                .ifPresent(email -> findMember.setEmail(email));
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));

        if (member.getRoles() != null && !member.getRoles().isEmpty()) {
            findMember.setRoles(member.getRoles());
        }

        return memberRepository.save(findMember);
    }

    // 특정 회원 정보 찾는 서비스 로직 구현
    public Member findMember(long memberId, long principalMemberId) {

        // 본인 id가 맞는지, 관리자 인지 아닌지 검증하는 메서드
        checkValidator.checkAdminAndCheckOwner(memberId, principalMemberId);

        return findVerifiedMember(memberId);
    }

    // 회원 전체 정보 조회 서비스 로직 구현
    public Page<Member> findMembers(int page, int size) {
        // page는 0 이하면 안됨
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이여야됨");
        }

        return memberRepository.findAll(PageRequest.of(page-1, size, Sort.by("memberId").descending()));
    }

    // 회원 삭제 서비스 로직 구현
    public void deleteMember(long memberId, long principalMemberId) {
        // 회원 존재하는지 검증
        Member findMember = findVerifiedMember(memberId);
        // 로그인한 회원이랑 맞는지 검증
        checkValidator.checkOwner(memberId, principalMemberId);
        // 관리자라면 관리자가 지울 수도 있어야하기에 관리자 인지 검증
        checkValidator.checkAdmin();

        // 근데 만약에 회원 상태가 이미 삭제된 상태라면? 그런 회원은 지울 수 없음
        if (findMember.getStatus() == Member.Status.MEMBER_QUIT) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        // 회원 탈퇴 상태로만 변경하고 질문도 비활성화 상태로 바꿔야함
        findMember.StatusChange();

        memberRepository.save(findMember);
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
