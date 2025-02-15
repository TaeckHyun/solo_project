package com.springboot.config;

import com.springboot.auth.filter.JwtAuthenticationFilter;
import com.springboot.auth.filter.JwtVerificationFilter;
import com.springboot.auth.handler.MemberAccessDeniedHandler;
import com.springboot.auth.handler.MemberAuthenticationEntryPoint;
import com.springboot.auth.handler.MemberAuthenticationFailureHandler;
import com.springboot.auth.handler.MemberAuthenticationSuccessHandler;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.AuthorityUtils;
import com.springboot.auth.utils.MemberDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final AuthorityUtils authorityUtils;
    private final MemberDetailsService memberDetailsService;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, AuthorityUtils authorityUtils, MemberDetailsService memberDetailsService) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.memberDetailsService = memberDetailsService;
    }

    @Bean
    // 보안 필터 체인을 구성하고 반환
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // frameOptions를 통해 클릭재킹 공격을 방어함
                // 클릭재킹이란? 공격자가 사용자에게 보이지 않는 프레임을 통해 악의적인 동작을 수행하는 공격 기법
                // sameOrigin은 동일한 출처(같은 도메인)에서만 페이지를 프레임에 포함할 수 있도록 허용
                .headers().frameOptions().sameOrigin() // HTTP 응답 헤더를 설정하는 부분
                .and() // 메서드 체이닝
                .csrf().disable() // CSRF 공격 방지 기능 비활성화
                /*
                   Cross-Origin Resource Sharing 란?
                   다른 출처(도메인, 프로토콜, 포트)의 리소스에 접근할 수 있도록 허용하는 매커니즘
                   브라우저는 보안상의 이유로 기본적으로 교차 출처 요청을 차단하지만 CORS를 통해서
                   이를 허용할 수 있음
                   Customizer.withDefaults() 는 Spring Security의 기본 CORS 설정을 사용함
                   기본 설정은 CorsConfigurationSource 빈을 통해 정의된 CORS 정책을 따름
                   만약 커스텀 CORS 설정이 필요하다면 CorsConfigurationSource 빈을 별도로 정의해야함
                */
                .cors(Customizer.withDefaults()) // CORS 설정을 활성화
                /*
                   아래 설정은 Spring Security의 세션 관리 방식을 정의하는 코드 라인
                   여기서 아래 코드는 세션을 생성하지 않고, 기존 세션도 사용하지 않는다는 설정
                   sessionManagement() -> 세션 관리 설정 시작 부분
                   sessionCreationPolicy() -> 세션 정책을 설정하는 부분
                   STATELESS -> 세션을 아예 사용하지 않음 (무상태)
                */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and() // 메서드 체이닝
                /*
                   apply() -> Spring Security의 HttpSecurity 설정에서 커스텀 필터를 적용하는 메서드
                   CustomFilterConfigurer 내부에서 JWT 인증 필터를 설정하고 Spring Security에 추가
                   JWT 기반 로그인 처리를 수행하는 필터가 동작하도록 만듬
                   즉 이 코드를 추가함으로써 JwtAuthenticationFilter가 Filter Chain에 등록
                   "/v11/auth/login"에서 JWT 기반 인증을 수행할 수 있음
                */
                .apply(new CustomFilterConfigurer())
                .and() // 메서드 체이닝
                .formLogin().disable() // 폼 로그인 비활성화
                .httpBasic().disable() // HTTP 기본 인증 비활성화
                /*
                   exceptionHandling() 이란?
                   Spring Security에서 예외(Exception) 처리를 설정하는 메서드
                   인증(Authentication) 및 인가(Authorization) 과정에서 발생하는 예외 처리 로직을 정의
                */
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint()) // 인증 실패 시 처리할 로직 등록
                .accessDeniedHandler(new MemberAccessDeniedHandler()) // 권한 관련(403) 예외 처리 핸들러 등록
                .and() // 메서드 체이닝
                /*
                   authorizeHttpRequests() 란?
                   Spring Security에서 HTTP 요청에 대한 인가(Authorization) 규칙을 설정하는 메서드
                   특정 URL 패턴에 대해 누가 접근할 수 있는지(Role/Permit 설정) 정의
                   antMatchers()를 사용하여 HTTP 요청별 접근 권한을 지정
                */
                .authorizeHttpRequests(authorize -> authorize // 람다식 사용해서 권한 설정 정의
                        // Member(회원) 관련 권한 설정
                        // 모든 사용자 접근 허용
                        .antMatchers(HttpMethod.POST, "/*/members").permitAll()
                        // 회원 정보 수정은 ROLE_USER 권한을 가진 사용자만 요청 가능
                        .antMatchers(HttpMethod.PATCH, "/*/members/**").hasRole("USER")
                        // 전체 회원 목록 조회는 ROLE_ADMIN 권한을 가진 관리자만 접근 가능
                        .antMatchers(HttpMethod.GET, "/*/members").hasRole("ADMIN")
                        // 특정 회원 정보 조회는 ROLE_USER 또는 ROLE_ADMIN 권한을 가진 사용자만 접근 가능
                        // hasAnyRole은 여러 Roles 중 하나만 있으면 됨
                        .antMatchers(HttpMethod.GET, "/*/members/**").hasAnyRole("USER", "ADMIN")
                        // 회원 탈퇴는 ROLE_USER 권한을 가진 사용자만 접근 가능
                        .antMatchers(HttpMethod.DELETE, "/*/members/**").hasRole("USER")

                        // 질문 생성 권한 설정
                        .antMatchers(HttpMethod.POST, "/*/questions").hasRole("USER")
                        // 질문 수정 권한 설정
                        .antMatchers(HttpMethod.PATCH, "/*/questions/**").hasRole("USER")
                        // 전체 질문 조회 권한 설정
                        .antMatchers(HttpMethod.GET, "/*/questions").hasAnyRole("USER", "ADMIN")
                        // 특정 질문 조회 권한 설정
                        .antMatchers(HttpMethod.GET, "/*/questions/**").hasAnyRole("USER", "ADMIN")
                        // 질문 삭제 권한 설정
                        .antMatchers(HttpMethod.DELETE, "/*/questions/**").hasRole("USER")

                        // 답변 생성 권한 설정
                        .antMatchers(HttpMethod.POST, "/*/answers").hasRole("ADMIN")
                        // 답변 수정 권한 설정
                        .antMatchers(HttpMethod.PATCH, "/*/answers/**").hasRole("ADMIN")
                        // 답변 삭제 권한 설정
                        .antMatchers(HttpMethod.DELETE, "/*/answers/**").hasRole("ADMIN")

                        // 좋아요 생성 권한 설정
                        .antMatchers(HttpMethod.POST, "/*/likes").hasAnyRole("USER", "ADMIN")
                        // 좋아요 삭제 권한 설정
                        .antMatchers(HttpMethod.DELETE, "/*/likes").hasAnyRole("USER", "ADMIN")

                        // 위에서 설정한 특정 패턴을 제외한 모든 요청 누구나 접근 가능
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    // Spring Security에서 비밀번호를 암호화하고 검증하는데 사용되는 PasswordEncoder 빈을 정의하는 메서드
    public PasswordEncoder passwordEncoder() {
        /* DelegatingPasswordEncoder 란?
           여러 종류의 passwordEncoder를 지원하는 위임(Delegating) 방식의 PasswordEncoder.
           다양한 암호화 알고리즘들을 동적으로 선택할 수 있음
           비밀번호가 저장될 때, 사용된 암호화 알고리즘을 식별 할 수 있는 접두사(Prefix)를 추가
           ex. {bcrypt}암호화된 문자열
           이 접두사를 통해 비밀번호를 검증할 때 적절한 PasswordEncoder를 선택함
        */
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // CorsConfiguration 객체 생성 -> CORS 설정을 정의하는데 사용
        CorsConfiguration configuration = new CorsConfiguration();

        /*
           setAllowedOrigins 란?
           교차 출처 요청을 허용할 출처(도메인)를 지정
           Arrays.asList("*") 이건 모든 출처를 허용한다는 의미
           특정 도메인만 허용 하려면 asList안에 내용을 바꿔야함 ex. asList("https://example.com") 처럼 지정
        */
        configuration.setAllowedOrigins(Arrays.asList("*"));

        /*
           setAllowedMethods 란?
           교차 출처 요청을 허용할 HTTP 메서드를 지정
           Arrays.asList("GET","POST","PATCH","DELETE")
           이건 GET, POST, PATCH, DELETE 메서드를 허용한다는 것
        */
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PATCH","DELETE"));

        /* UrlBasedCorsConfigurationSource 란?
           특정 URL 패턴에 따라 다른 CORS 설정을 적용할 수 있도록 도와주는 객체
           각 URL(엔드포인트)마다 CORS 정책을 다르게 설정할 수 있도록 관리하는 역할
        */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 URL("/**")에 동일한 CORS 설정(Configuration)을 적용한다는 의미
        // 즉 위에 만든 커스텀 CorsConfigurationSource 을 모든 URL에 적용한다는 것
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /*
       Spring Security의 설정을 커스터마이징할 수 있도록 해주는 클래스
       AbstractHttpConfigurer<T, B>를 상속받아 HttpSecurity 설정을 변경할 수 있음
       그리고 JWT 인증 필터(JwtAuthenticationFilter)를 Spring Security의 필터 체인에 추가하는 역할을 함
    */

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        // configure -> Spring Security 설정을 수정하는 메서드
        // HttpSecurity builder 객체를 통해 Spring Security의 보안 설정을 변경할 수 있음
        public void configure(HttpSecurity builder) throws Exception {
            /*
               현재 보안 설정에서 AuthenticationManager 객체를 가져옴
               여기서 AuthenticationManager 란?
               사용자의 로그인 요청을 받아 인증을 처리하는 핵심 객체
               HttpSecurity에서 공유되는 객체(getSharedObject)로 가져올 수 있음
            */
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            /*
               JwtAuthenticationFilter 란?
               JWT 기반 인증을 수행하는 커스텀 필터
               이 필터는 사용자의 로그인 요청을 가로채서 JWT를 발급하거나 검증하는 역할을 함
            */
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);

            // 로그인 요청을 처리하는 EndPoint를 "v11/auth/login"으로 설정
            jwtAuthenticationFilter.setFilterProcessesUrl("/v11/auth/login");

            // JWT 기반 인증 필터의 성공/실패 핸들러 및 검증 필터 설정
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());

            // 로그인 인증이 실패했을 때 실행될 핸들러를 설정
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            // JWT 토큰을 검증하는 필터를 생성
            // 유효한 사용자라면 SecurityContext에 인증 정보를 저장하는 역할을 함
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils, memberDetailsService);

            // HttpSecurity 필터 체인에 jwtAuthenticationFilter 를 추가
            // 로그인 요청이 들어오면 이 필터가 실행되도록 설정
            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);

        }
    }
}
