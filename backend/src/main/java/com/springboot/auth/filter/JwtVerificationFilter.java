package com.springboot.auth.filter;

import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.AuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
   Spring Security에서 JWT(JSON Web Token)를 검증하는 전용 필터
   즉, 클라이언트가 보낸 요청에 포함된 JWT를 확인하고, 검증된 정보를 기반으로 사용자 인증을 수행하는 역할
*/
/*
   OncePerRequestFilter 란?
   Spring Security에서 모든 요청에 대해 단 한 번만 실행되는 필터 즉, 사용자가 요청할 때마다 JWT를 검증하는 로직이 한 번씩 실행됨
   OncePerRequestFilter를 사용하면 필터가 여러 번 실행되는 문제를 방지 할 수 있음
*/
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final AuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer,
                                 AuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Override
    // Spring Security의 필터에서 요청을 처리할 때 자동으로 실행되는 메서드

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    // 현재 필터가 처리한 후, 다음 필터로 요청을 넘기기 위한 객체
                                    FilterChain filterChain) throws ServletException, IOException {
        /*
           JWT를 검증하는 역할을 함
           request의 Authorization 헤더에서 JWT를 추출
           토큰의 서명(Signature)과 유효기간(Expiration)을 검증
           검증이 성공하면 JWT의 Payload(클레임, Claims)를 반환
        */
        try {
            Map<String, Object> claims = verifyJws(request);
            // 검증된 claims(JWT에 포함된 사용자 정보)를 Spring Security의 SecurityContext에 저장
            // 이후 컨트롤러에서 해당 유저가 인증된 상태로 요청을 처리할 수 있도록 함
            setAuthenticationToContext(claims);
        }   /*
               SignatureException 이란?
               JWT의 서명 검증에 실패한 경우 발생하는 예외
               요청 객체에 예외 정보를 저장하여 이후 예외 처리를 수행할 수 있도록 함
            */
        catch (SignatureException se) {
            request.setAttribute("exception", se);
        }
            // JWT의 유효기간(Expiration)이 만료된 경우 발생하는 예외
            // 클라이언트가 만료된 토큰을 보내면 인증 실패로 처리됨
        catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        }
            // 기타 예외 (예: JWT 형식이 잘못되었거나, 예상치 못한 오류 발생)
            // 보안상의 이유로 예외 정보를 지나치게 상세히 제공하지 않도록 주의해야 함
        catch (Exception e) {
            request.setAttribute("exception", e);
        }

        // 현재 필터가 요청을 처리한 후, 다음 필터로 요청을 전달하여 필터 체인이 계속 진행되도록 함
        filterChain.doFilter(request, response);
    }

    @Override
    // 이 메서드는 현재 요청에 대해 JWT 검증 필터를 적용할지 말지를 결정하는 역할을 함
    // 반환값이 true이면 필터를 실행하지 않으며, false이면 필터를 실행
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 클라이언트가 보낸 요청 헤더에서 "Authorization" 값을 가져옴
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더가 없거나, 값이 "Bearer "로 시작하지 않으면 필터를 적용하지 않음
        return authorization == null || !authorization.startsWith("Bearer");
    }

    // 이 메서드는 요청에서 JWT를 추출하여 유효성을 검증하고, 토큰에 포함된 정보를 반환하는 역할
    private Map<String, Object> verifyJws(HttpServletRequest request) {
        // Authorization 헤더에서 JWT 값을 가져오되, "Bearer " 문자열을 제거하여 실제 토큰만 추출
        String jws = request.getHeader("Authorization").replace("Bearer ", "");

        // JWT 서명을 검증할 때 사용할 Base64 인코딩된 비밀 키를 가져옴
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        // JWT를 파싱하여 클레임(Claims, JWT의 Payload 부분)을 가져옴
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        // 파싱한 클레임 데이터를 반환 (사용자의 정보 및 권한이 담겨 있음)
        return claims;
    }

    // JWT에서 추출한 사용자 정보를 Spring Security의 인증 컨텍스트(SecurityContextHolder)에 저장하는 역할
    private void setAuthenticationToContext(Map<String, Object> claims) {
        // JWT의 클레임(Claims)에서 "username" 값을 가져옴
        String username = (String) claims.get("username");

        // JWT의 "roles" 값을 가져와서 Spring Security의 GrantedAuthority 객체 리스트로 변환
        // "roles"는 보통 ["ROLE_USER", "ROLE_ADMIN"] 같은 리스트 형태
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));

        // 인증(Authentication) 객체 생성 (비밀번호는 필요 없으므로 null 처리)
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

        // 현재 스레드의 SecurityContext에 인증 객체를 저장하여, 이후 요청에서도 인증된 사용자로 인식되게 함
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}