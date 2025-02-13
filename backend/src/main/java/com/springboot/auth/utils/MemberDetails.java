package com.springboot.auth.utils;

import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Member를 상속 받아서 회원의 기본 정보를 그대로 포함
// 상속과 동시에 UserDetails 인터페이스를 구현
public class MemberDetails extends Member implements UserDetails {
    private final List<GrantedAuthority> authorities;

    // Member 객체를 인자로 받아서 이를 기반으로 HelloUserDetails 객체 초기화
    // 이것을 통해 기존 회원 정보 + Spring Security가 요구하는 정보를 모두 포함한 객체 생성
    MemberDetails(Member member, List<GrantedAuthority> authorities) {
        super();
        setMemberId(member.getMemberId());
        setEmail(member.getEmail());
        setPassword(member.getPassword());
        setRoles(member.getRoles());
        this.authorities = authorities;
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
        return authorities;
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