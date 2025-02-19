import axios from "axios";

// 이 코드는 Axios 인스턴스를 생성해서 API 요청을 쉽게 관리할 수 있도록 설정
export const apiClient = axios.create({ // axios.create 를 쓰면 공통 설정을 적용한 AXios 인스턴스를 만들 수 있음
    baseURL: "http://localhost:8080/v1", // apiClient 를 쓰면 모든 API 요청의 기본 URL 설정
    withCredentials: true // 쿠키 기반 인증 정보를 요청에 포함하도록 설정하는 옵션
});

// 로그인 API 요청 함수
export const login = async (data) => {
    const loginResponse = await apiClient.post("/auth/login", data);

    // 응답 헤더에서 AccessToken, RefreshToken 받아옴
    const accessToken = loginResponse.headers["authorization"];
    const refreshToken = loginResponse.headers["refresh"];

    // 콘솔로 토큰 확인
    console.log("AccessToken : ", accessToken);
    console.log("refreshToken : ", refreshToken);

    if (accessToken) {
        localStorage.setItem("accessToken", accessToken.replace("Bearer ", "")); // 로컬 스토리지에 저장
    }
    if (refreshToken) {
        localStorage.setItem("refreshToken", refreshToken);
    }

    return loginResponse.data;
};