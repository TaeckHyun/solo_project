package com.springboot.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenizer {
    @Getter
    @Value("${jwt.key}") // yml 파일 경로의 값이 필드로 들어감
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}") // yml 파일 경로의 값이 필드로 들어감
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}") // yml 파일 경로의 값이 필드로 들어감
    private int refreshTokenExpirationMinutes;

    /*
       주어진 Secret Key를 Base64로 인코딩하여 반환하는 메서드
       입력된 secretKey를 UTF-8 방식의 바이트 배열로 반환
       그 후에 바이트 배열을 Base64 형식의 문자열로 인코딩함
       이렇게 인코딩된 문자열은 JWT를 서명하거나 검증할 때 사용됨
    */
    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT AccessToken을 생성하는 메서드, 인증 토큰을 만드는 것
    public String generateAccessToken(Map<String, Object> claims, // Map을 쓰는 이유는 JWT의 Payload는 key-value 형태의 데이터를 저장 이를 위해서 Map<String, Object>로 사용하는 것
                                      String subject, // JWT의 주제, 제목, 보통 사용자 고유 식별자로 씀
                                      Date expiration, // JWT의 만료 시간
                                      String base64EncodedSecretKey) { // 서명을 위한 비밀 키

        // Base64로 인코딩된 secretkey를 디코딩해서 Key 객체로 반환
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        // 토큰 발행을 위해 Jwts.builder()를 사용해서 JWT를 생성하는 객체를 만듬
        return Jwts.builder()
                .setClaims(claims) // claims에 들어있는 key-value 데이터를 JWT의 Payload에 추가
                .setSubject(subject) // 받아온 subject를 설정하고 Payload에 추가
                // Calendar.getInstance().getTime()은 현재 시스템의 날짜와 시간을 가지는 Calender 객체를 생성하고 현재 시간을 Date 객체로 변환해줌
                .setIssuedAt(Calendar.getInstance().getTime()) // JWT가 생성된 시간을 기록, 클라이언트가 토큰을 보낼 때, 서버에서 이 토큰이 유효한지 체크하는 데 사용
                .setExpiration(expiration) // JWT 만료 시간을 설정
                .signWith(key) // signWith(key)를 이용해서 JWT에 서명을 추가
                .compact(); // JWT를 문자열 형태로 변환하여 반환
    }

    /*
       JWT RefreshToken을 생성하는 메서드
       AccessToken이 만료 되었을 때 새로운 AccessToken을 발급받기 위해 사용됨
       AccessToken 보다 긴 유효시간을 가져야함, 그리고 서버는 RefreshToken을 검증해서 새로운 AccessToken을 발급할지 판단
    */
    // 여기서 expiration은 리프레시 토큰의 만료 시간
    public String generateRefreshToken(String subject, Date expiration, String base64EncodedSecretKey) {
        // base64EncodedSecretKey를 디코딩하여 Key 객체로 변환
        // Key 객체는 JWT의 서명(Signature) 생성에 사용됨, JWT의 변조 방지를 위해 필수
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        // JWT를 생성할 빌더 객체 만듬
        return Jwts.builder()
                .setSubject(subject) // 사용자의 고유 ID -> 여기서는 Email을 넣고 Payload에 추가
                .setIssuedAt(Calendar.getInstance().getTime()) // JWT가 생성된 시간을 기록, 클라이언트가 토큰을 보낼 때, 서버에서 이 토큰이 유효한지 체크하는 데 사용
                .setExpiration(expiration) // JWT 만료 시간 설정
                .signWith(key) // JWT에 서명을 추가
                .compact(); // JWT를 문자열 형태로 변환하여 반환
    }

    /*
       이 메서드는 JWT 토큰을 파싱(복호화)해서 포함된 클레임(Claims) 정보를 추출하는 역할을 함
       즉, JWT 토큰을 입력받아 서명을 검증하고, Payload(Claims)를 반환
       쉽게 말하면 JWT가 유효한지 확인하고, 안에 들어있는 정보(Claims)를 가져오는 메서드
    */
    // Jws는 서명(Signature)가 포함된 JWT를 의미함, Claims는 JWT의 Payload
    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        // BASE64로 인코딩된 비밀 키를 실제 HMAC 서명 키(Key 객체)로 변환
        // getKeyFromBase64EncodedKey는 Base64 디코딩해서 서명 검증에 사용할 키 생성하는 메서드
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        // JWT를 해석할 JWT 파서(parser)를 생성
        Jws<Claims> claims = Jwts.parserBuilder() // JWT를 검증하고 해석하는 빌더 객체 만듬
                .setSigningKey(key) // 서명을 검증할 키를 지정, 즉 서명 검증을 위한 비밀 키 설정
                .build() // JWT 검증을 수행할 파서를 생성
                .parseClaimsJws(jws); // JWT를 해석하고 클레임을 추출
        return claims;
    }

    /*
       JWT의 만료 시간을 설정하기 위한 메서드
       현재 시간을 가져와서, 지정된 expirationMinutes(분 단위)만큼 더한 후 만료 시간을 계산
       반환된 Date 객체는 JWT의 expiration 클레임에 사용됨
    */
    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance(); // 현재 시간을 기준으로 하는 Calendar 객체 생성

        calendar.add(Calendar.MINUTE, expirationMinutes); // 현재 시간에 expirationMinutes 만큼 추가하여 만료 시간 설정

        Date expiration = calendar.getTime(); // Calendar 객체에서 Date 타입으로 변환하여 만료 시간 반환

        return expiration;
    }

    /*
       JWT의 서명을 검증하기 위한 Secret Key를 생성하는 메서드
       Base64로 인코딩된 문자열을 디코딩하여 바이트 배열로 변환
       변환된 바이트 배열을 사용해 Keys.hmacShaKeyFor() 를 통해 HMAC SHA 알고리즘을 사용하는 키 생성
       생성된 키는 JWT 서명(Signature)을 검증할 때 사용됨
    */
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey); // Base64 인코딩된 키를 디코딩하여 바이트 배열로 변환
        Key key = Keys.hmacShaKeyFor(keyBytes); // 디코딩된 바이트 배열을 기반으로 HMAC SHA 암호화 키 생성

        return key;
    }
}
