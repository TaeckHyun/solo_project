package com.springboot.auth.handler;

import com.springboot.auth.utils.ErrorResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
// Spring Security에서 인증되지 않은 사용자가 보호된 리소스에 접근할 때 실행되는 예외 처리기
// 즉, 로그인이 필요한 API에 비로그인 상태로 접근하면 실행됨
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    // 이 메서드는 Spring Security가 인증되지 않은 사용자의 요청을 거부할 때 자동 실행
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         /*
                           AuthenticationException 이란?
                           Spring Security에서 인증(Authentication) 과정 중 발생하는 예외를 처리하는 클래스
                           즉, 사용자가 로그인하거나 JWT 검증을 시도할 때 문제가 발생하면 이 예외가 발생
                         */
                         AuthenticationException authException) throws IOException, ServletException {

        // 요청 속성(request attribute)에서 "exception"이라는 키로 저장된 예외 객체를 가져옴
        Exception exception = (Exception) request.getAttribute("exception");

        // 클라이언트에게 401 Unauthorized 응답을 보냄
        ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);

        // 예외 메시지를 로그로 남김
        logExceptionMessage(authException, exception);
    }

    // 예외 메시지를 로그로 출력하는 메서드
    private void logExceptionMessage(AuthenticationException authException, Exception exception) {
        // request에서 가져온 exception이 존재하면 해당 예외 메시지를 출력
        // 없으면 authException의 메시지를 출력
        String message = exception != null ? exception.getMessage() : authException.getMessage();

        // 로그 경고 메시지 출력
        log.warn("Unauthorized error happened: {}", message);
    }
}
