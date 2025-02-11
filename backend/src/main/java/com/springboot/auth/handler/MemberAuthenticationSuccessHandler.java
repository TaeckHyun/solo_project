package com.springboot.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Lombok 라이브러리에서 제공하는 어노테이션으로, 자동으로 Log 객체를 생성해줌
// log.error(), log.info() 같은 로깅 기능 사용가능
@Slf4j

/*
   Spring Security에서 로그인 인증이 성공했을 때 실행되는 핸들러
   로그인 성공 시 로그를 남기고, 이후 필요한 추가 작업을 수행할 수 있도록 구성되어 있음
*/
/*
   AuthenticationSuccessHandler 란?
   Spring Security에서 로그인 인증이 성공했을 때 실행되는 핸들러 인터페이스
   onAuthenticationSuccess() 메서드를 구현하여 로그인 성공 후 어떤 작업을 할지 정의
*/
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, // 사용자 로그인 요청 정보
                                        HttpServletResponse response, // 클라이언트에게 보낼 응답 객체
                                        // 인증 성공 정보를 담고 있는 객체
                                        Authentication authentication) throws IOException, ServletException {
        // 로그인 성공 로그를 출력
        log.info(" Authentication Success!!");
    }
}
