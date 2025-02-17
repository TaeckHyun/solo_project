package com.springboot.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.auth.dto.LoginDto;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.member.entity.Member;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// 클라이언트의 로그인 인증 정보를 직접 수신하여 인증 처리의 엔트리포인트 역할을 하는 클래스
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenizer jwtTokenizer) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
    }

    // SneakyThrows는 try-catch 없이도 checked exception이 발생하면 자동으로 예외를 던짐
    @SneakyThrows // 예외를 자동으로 처리해주는 Lombok 어노테이션
    @Override
    /*
       Spring Security에서 로그인 요청을 처리하는 메서드로 사용자가 로그인할때 입력한 아이디, 비번을 검증하는 역할
       로그인 성공 시에 Authentication 객체를 반환하고, 실패 시 예외 발생시킴
    */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // 클라이언트가 보낸 JSON 데이터를 LoginDto 객체로 변환하기 위해 사용함
        /*
           ObjectMapper 란?
           Jackson 라이브러리 클래스이며, Json 데이터를 Java 객체로 변환 및 역직렬화하는 역할
        */
        ObjectMapper objectMapper = new ObjectMapper();
        /*
           클라이언트가 보낸 요청의 본문(body)을 읽어서 LoginDto 객체로 변환
           request.getInputStream() -> HTTP 요청 Body 에서 데이터를 가져옴
           objectMapper.readValue(입력 스트림, 변환할 클래스) -> JSON 데이터를 LoginDto 객체로 매핑
           EX) 만약 사용자가 보낸 JSON 데이터가 아래처럼 보냈다고 가정하면
           {
                "username": "user123",
                "password": "securePass"
           }
           위 JSON이 LoginDto loginDto = new LoginDto("user123", "securePass");
           이런 형식의 LoginDto 객체로 변환됨 즉, 사용자가 입력한 아이디, 비번이 loginDto 객체에 저장됨
        */
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);


        /*
           사용자의 아이디(username)와 비밀번호(password)를 이용해 인증 요청 객체를 생성
           UsernamePasswordAuthenticationToken: Spring Security의 인증 토큰 객체
           인증 요청을 생성해서 AuthenticationManager에게 전달하면,
           AuthenticationManager가 DB에서 사용자 정보 확인 후 비밀번호 검증을 진행함.
        */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        /*
           AuthenticationManager를 통해 실제 인증 수행
           authenticationManager.authenticate(authenticationToken) 란?
           AuthenticationManager가 사용자의 아이디(username)와 비밀번호(password)를 검증함
           인증 절차:
           1) 사용자의 username으로 DB에서 정보를 조회
           2) 입력한 password와 DB의 password가 일치하는지 확인
           3) 일치하면 인증된 Authentication 객체를 반환
           4) 일치하지 않으면 예외 발생 (BadCredentialsException)

           즉, 로그인 성공 시 인증된 Authentication 객체가 반환되며,
           로그인 실패 시 예외가 발생함
    */
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    /*
       이 메서드는 로그인 성공 이후 실행되는 메서드로, 사용자에게
       AccessToken과 RefreshToken을 발급하는 역할을 함
       Spring Security에서 인증이 성공하면 실행되는 필터 메서드로
       JWT를 생성하고 응답 헤더에 추가하는 핵심 로직 담당
    */
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException {
        /*
        authResult.getPrincipal()은 현재 인증된 사용자 정보를 반환
        Principal 객체에는 로그인한 사용자의 주요 정보(username, roles)가 담겨 있음
        Member 엔티티 클래스는 사용자의 ID, 권한, Email 들의 정보를 가지고 있음
        즉, 현재 로그인한 사용자의 정보를 Member 변수에 저장하기 위해 강제 형변환
        */
        Member member = (Member) authResult.getPrincipal();

        // delegateAccessToken() 메서드는 사용자 정보를 기반으로 AccessToken 생성
        // 즉, 로그인한 사용자 정보를 기반으로 JWT 생성해서 accessToken 변수에 저장
        String accessToken = delegateAccessToken(member);

        // delegateRefreshToken() 메서드는 사용자 정보를 기반으로 RefreshToken 생성
        // 이것 또한 로그인한 사용자 정보를 기반으로 JWT 생성해서 refreshToken 변수에 저장
        String refreshToken = delegateRefreshToken(member, accessToken);

        /*
           클라이언트가 API 요청 시 JWT를 인증 헤더에 포함하도록 응답에 추가
           Authorization -> HTTP 요청 헤더에서 인증 정보를 전달하는 표준 키
           "Bearer " + accessToken -> JWT를 Bearer Token 방식으로 설정
           여기서 "Bearer " 은 JWT 토큰을 사용하는 표준 양식
        */
        response.setHeader("Authorization", "Bearer " + accessToken);

        /*
           Refresh -> Refresh Token을 전달하기 위한 커스텀 헤더
           클라이언트는 이 Refresh Token을 저장하고, Access Token이 만료되면 새로운 Access Token을 요청하는데 사용
           즉, 클라이언트가 Access Token을 갱신할 때 사용할 Refresh Token을 응답에 포함함
        */
        response.setHeader("Refresh", refreshToken);

        // getSuccessHandler().onAuthenticationSuccess()는 로그인 성공 후 추가적인 작업을 수행하도록 설정 가능
        // 로그인 성공 시 커스텀 동작을 수행할 수 있도록 Security의 Success Handler를 실행
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }


    // 로그인한 사용자 정보를 기반으로 Access Token(JWT)을 생성하는 메서드
    private String delegateAccessToken(Member member) {
        /*
           JWT에 포함될 사용자 정보를 저장하는 claims 객체 생성
           claims는 JWT의 Payload 부분에 들어가는 데이터
           Map<String, Object> 타입을 쓴 이유는 Payload가 key-value 형태기 때문에
           여기서 저장한 데이터는 JWT 생성 시 Payload에 포함되어 클라이언트와 서버가 공유
        */
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getEmail());
        claims.put("roles", member.getRoles());

        // JWT의 주체 설정 JWT 토큰이 어떤 사용자에게 발급되었는지 나타냄, 보통 사용자의 고유 ID나 Email씀 여기선 Email
        String subject = member.getEmail();

        /*
           Access Token의 만료 시간 설정
           jwtTokenizer.getAccessTokenExpirationMinutes() 를 통해서 설정된 만료 시간을 가져옴
           jwtTokenizer.getTokenExpiration(시간) 을 통해 현재 시간 기준으로 만료 시간을 계산함
        */
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        /*
           JWT 서명을 위한 Secret Key를 Base64로 인코딩
           JWT는 서명(Signature)을 포함해야 무결성 보장
           jwtTokenizer.getSecretKey() -> 서명에 사용할 원래 비밀 키 가져오기
           jwtTokenizer.encodeBase64SecretKey(비밀 키) -> Base64로 인코딩하여 사용
        */
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        /*
           jwtTokenizer.generateAccessToken 메서드는 JWT를 생성하는 역할
           claims -> JWT에 포함할 사용자 정보 (이메일, 역할)
           subject -> 토큰 주제 (사용자 이메일)
           expiration -> 만료 시간
           base64EncodedSecretKey -> JWT 서명을 위한 Secret Key

           JWT 생성 과정
           claims, subject, expiration 값을 포함한 JWT 생성
           base64EncodedSecretKey 를 사용해 HMAC SHA256 등의 알고리즘으로 서명
           최종적으로 서명된 JWT 문자열 반환, 결과적으로 accessToken 변수에 JWT 저장
        */
        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    // Refresh Token 을 생성하는 메서드
    private String delegateRefreshToken(Member member, String accessToken) {
        // Refresh Token의 주제(subject) 설정
        // 일반적으로 subject는 토큰을 발급받는 사용자의 고유 식별자(Ex. 이메일)로 설정됨
        String subject = member.getEmail();

        // Refresh Token의 만료 시간을 설정
        // 설정된 Refresh Token의 만료 시간을 가져옴
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());

        // Secret Key를 가져와서 BASE64 형식으로 인코딩
        // JWT 서명을 생성할 때 사용할 비밀키를 가져와서 BASE64로 전환하는 것
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        // Refresh Token을 생성하는 메서드를 호출하여 Refresh Token을 생성함
        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey, accessToken);

        // 완성된 Refresh Token 반환
        return refreshToken;
    }

}