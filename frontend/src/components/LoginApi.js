import axios from "axios";

// 요청 시 Access Token 자동 추가
export const apiClient = axios.create({
    baseURL: "http://localhost:8080/v1",
    withCredentials: true // 쿠키 허용
});

apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// 로그인 요청 함수
export const login = async (data) => {
    const response = await apiClient.post("/auth/login", data);

    // localStorage에 토큰 저장
    localStorage.setItem("accessToken", response.data.accessToken);
    localStorage.setItem("refreshToken", response.data.refreshToken);

    return response.data;
};

// 로그아웃 요청 함수
export const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
};
