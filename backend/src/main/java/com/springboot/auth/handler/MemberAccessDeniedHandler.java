package com.springboot.auth.handler;

import com.springboot.auth.utils.ErrorResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
// 사용자가 접근 권한이 없는 리소스에 접근하려고 할 때 실행되는 핸들러
public class MemberAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       /*
                          AccessDeniedException 이란?
                          Spring Security에서 사용자가 접근 권한이 없는 리소스에 접근하려고 할 때 발생하는 예외(Exception)
                          HTTP 상태 코드: 403 Forbidden, 인증(로그인)은 성공했지만, 권한이 부족할 때 발생
                          Spring Security가 자동으로 던지는 예외
                       */
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 클라이언트에게 403 Forbidden 응답을 전송
        // 사용자가 인증은 되었지만, 특정 리소스에 대한 접근 권한이 없을 때 발생하는 오류
        ErrorResponder.sendErrorResponse(response, HttpStatus.FORBIDDEN);

        // 로그 경고 메시지를 출력하여 접근 거부(Forbidden) 오류가 발생한 이유를 기록
        log.warn("Forbidden error happened: {}", accessDeniedException.getMessage());
    }
}
