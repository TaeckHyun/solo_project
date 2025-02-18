import axios from "axios";

export const apiClient = axios.create({
    baseURL: "http://localhost:8080/v1",
    withCredentials: true
});

// 회원가입 요청 함수
export const register = async (data) => {
    const response = await apiClient.post("/members", data);
    return response.data;
};

// 사용자 정보 가져오기 (예제)
export const getUserProfile = async () => {
    const response = await apiClient.get("/members/profile");
    return response.data;
};
