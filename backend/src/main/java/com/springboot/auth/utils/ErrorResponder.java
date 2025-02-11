package com.springboot.auth.utils;

import com.google.gson.Gson;
import com.springboot.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 클라이언트에게 JSON 형식의 오류 응답을 보내는 유틸리티 클래스
public class ErrorResponder {
    // 클라이언트에게 HTTP 상태 코드와 함께 JSON 형식의 오류 응답을 반환하는 메서드
    public static void sendErrorResponse(HttpServletResponse response, HttpStatus status) throws IOException {
        // Gson 라이브러리를 사용하여 객체를 JSON 문자열로 변환할 수 있는 객체 생성
        Gson gson = new Gson();

        // 현재 요청에서 발생한 오류 정보를 포함하는 ErrorResponse 객체 생성
        // ErrorResponse.of(status)는 주어진 HTTP 상태 코드(status)를 기반으로 에러 응답 객체를 만듦
        ErrorResponse errorResponse = ErrorResponse.of(status);

        // HTTP 응답의 Content-Type을 JSON으로 설정
        // 클라이언트가 JSON 형식의 데이터를 받도록 응답 헤더를 설정함
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // HTTP 상태 코드 설정 (예: 401 Unauthorized, 403 Forbidden 등)
        response.setStatus(status.value());

        // HTTP 응답 본문(Body)에 JSON 형식의 에러 메시지를 작성하여 클라이언트에게 전송
        // gson.toJson(errorResponse, ErrorResponse.class)는 errorResponse 객체를 JSON 문자열로 변환
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));
    }
}
