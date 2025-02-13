package com.springboot.auth.utils;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
        return new MemberDetails(findMember);
    }

    // Member를 상속 받아서 회원의 기본 정보를 그대로 포함
    // 상속과 동시에 UserDetails 인터페이스를 구현
    @Getter
    @AllArgsConstructor
    public final class MemberDetails extends Member implements UserDetails {
        // Member 객체를 인자로 받아서 이를 기반으로 HelloUserDetails 객체 초기화
        // 이것을 통해 기존 회원 정보 + Spring Security가 요구하는 정보를 모두 포함한 객체 생성
        MemberDetails(Member member) {
            setMemberId(member.getMemberId());
            setEmail(member.getEmail());
            setPassword(member.getPassword());
            setRoles(member.getRoles());
        }

        @Override
        // Collection을 사용할때 Type이 Generic이고 GrantedAuthority를 상속받는데
        // ?는 와일드 카드로 어떤 구체적인 타입이 들어 올 수 있다는 것을 의미
        // 하지만 여기선 extends GrantedAuthority라고 붙어서
        // 그 구체적인 타입은 반드시 GrantedAuthority 이거나 그 하위 클래스여야만함
        // 이렇게 써야하는 이유는 만약 타입을 그냥 GrantedAuthority로 해버리면
        // SimpleGrantedAuthority이라는 구현체들을 못 담음
        // 그래서 그런 것 또한 생각해서 와일드 카드 사용
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // createAuthorities를 통해 문자열 형태의 역할 목록을 GrantedAuthority 객체들의 리스트로 변환
            // 그 후 return을 통해 GrantedAuthority 객체들의 컬렉션을 반환
            // Spring Security가 사용자 권한을 확인할 수 있게 함
            return authorityUtils.createAuthorities(this.getRoles());
        }

        @Override
        // 사용자의 식별자를 반환하는 메서드
        public String getUsername() {
            return getEmail();
        }

        // 밑에 4개의 메서드는 계정의 상태를 나타냄

        @Override
        // 계정이 만료되지 않았음을 의미
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        // 계정이 잠겨 있지 않음을 의미
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        // 인증 정보가 만료되지 않았음을 의미
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        // 계정이 활성화되어 있음을 의미
        public boolean isEnabled() {
            return true;
        }
    }
}
