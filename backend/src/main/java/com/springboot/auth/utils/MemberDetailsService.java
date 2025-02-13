package com.springboot.auth.utils;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
/*
   Spring Security에서 사용자의 로그인 인증을 처리하는 가장 단순하고 효과적인 방법은
   데이터베이스에서 사용자의 크리덴셜을 조회한 후,
   조회한 크리덴셜을 AuthenticationManager에게 전달하는 Custom UserDetailsService를 구현하는 것
*/

/* UserDetails는 사용자 정보를 담는 인터페이스
   Spring Security에서 인증된 사용자의 정보를 나타내며, 이 인터페이스를 통해 사용자의 아이디, 비밀번호, 권한, 계정 상태 정보들을 제공
   UserDetailsService는 Spring Security에서 제공하는 인터페이스 중 하나, 주로 사용자 인증을 위해 사용자 정보를 불러오는 역할을 담당함
   UserDetailsService 인터페이스 안에 메서드는 loadUserByUsername
   이 메서드의 역할은 입력된 username을 기반으로 DB나 다른 저장소에서 사용자 정보를 조회하여, UserDetails 객체 반환
*/
@Component
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final AuthorityUtils authorityUtils;

    public MemberDetailsService(MemberRepository memberRepository, AuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.authorityUtils = authorityUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 여기서 username은 이메일임 현재 전체 코드에서는 이메일이 username 역할을 하는 중
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        Member findMember = optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities(findMember.getRoles());
        return new MemberDetails(findMember, authorities);
    }
}
