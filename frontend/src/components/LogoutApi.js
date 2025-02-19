import axios from "axios";

// 이 코드는 Axios 인스턴스를 생성해서 API 요청을 쉽게 관리할 수 있도록 설정
export const apiClient = axios.create({ // axios.create 를 쓰면 공통 설정을 적용한 AXios 인스턴스를 만들 수 있음
    baseURL: "http://localhost:8080", // apiClient 를 쓰면 모든 API 요청의 기본 URL 설정
    withCredentials: true // 쿠키 기반 인증 정보를 요청에 포함하도록 설정하는 옵션
});

export const logout = async () => {
    try {
        const accessToken = localStorage.getItem("accessToken");

        // 백엔드에 로그아웃 요청
        await apiClient.post(
            "/auth/logout",
        {},
    {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    });

        // 로컬 스토리지에서 토큰 삭제
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

        console.log("로그아웃 완료! 토큰 삭제됨.");
    } catch (error) {
        console.error("로그아웃 실패:", error);
        throw error;
    }
};