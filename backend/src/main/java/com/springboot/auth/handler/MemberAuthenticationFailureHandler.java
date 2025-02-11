package com.springboot.auth.handler;

import com.google.gson.Gson;
import com.springboot.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Lombok 라이브러리에서 제공하는 어노테이션으로, 자동으로 Log 객체를 생성해줌
// log.error(), log.info() 같은 로깅 기능 사용가능
@Slf4j
/*
   Spring Security에서 로그인 인증이 실패했을 때 실행되는 핸들러
   즉, 로그인 실패시 로그를 남기고, 클라이언트에게 인증 실패 응답(JSON)을 반환하는 역할을 함
*/
/*
   Spring Security에서 로그인 실패 시 실행되는 핸들러 인터페이스
   onAuthenticationFailure() 메서드를 구현해서 로그인 실패 시 어떤 작업을 할지 정의함
*/
public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    // 로그인 실패 시 자동으로 실행되는 메서드
    public void onAuthenticationFailure(HttpServletRequest request, // 사용자의 로그인 요청 정보
                                        HttpServletResponse response, // 클라이언트에게 보낼 응답 객체
                                        // 인증 실패 원인을 담고 있는 객체
                                        AuthenticationException exception) throws IOException, ServletException {

        // 로그인 실패 원인 로그 기록
        log.error("# Authentication failed: {}", exception.getMessage());

        // 로그인 실패 응답을 클라이언트에게 전송하는 메서드 호출
        sendErrorResponse(response);
    }

    // 로그인 실패 시 JSON 형식의 에러 응답 생성하고 클라이언트에게 전송하는 역할
    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        Gson gson = new Gson(); // Gson 라이브러리 사용하여 객체를 JSON 문자열로 변환 가능

        // ErrorResponse 객체를 생성, HTTP 상태 코드 401 (Unauthorized) 정보를 담음
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED);

        // 응답의 ContentType을 JSON 형식으로 설정, 클라이언트가 JSON 응답을 받을 수 있도록 함
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // HTTP 상태 코드를 401 (Unauthorized)로 설정 (로그인 실패 상태)
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        /*
           gson.toJson(errorResponse, ErrorResponse.class) 을 통해서 ErrorResponse 객체를 JSON 문자열로 반환
           response.getWriter().write() 을 통해서 변환된 JSON 데이터를 응답 본문에 추가하여 클라이언트에게 반환
        */
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));
    }
}
