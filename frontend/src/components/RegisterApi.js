import axios from "axios";

// 이 코드는 Axios 인스턴스를 생성해서 API 요청을 쉽게 관리할 수 있도록 설정
export const apiClient = axios.create({ // axios.create 를 쓰면 공통 설정을 적용한 AXios 인스턴스를 만들 수 있음
    baseURL: "http://localhost:8080/v1", // apiClient 를 쓰면 모든 API 요청의 기본 URL 설정
    withCredentials: true // 쿠키 기반 인증 정보를 요청에 포함하도록 설정하는 옵션
});

// 회원가입 API 요청 함수
export const register = async (data) => { // 여기서 data는 이메일, 이름, 전화번호, 비밀번호를 담은 객체
    // 서버 localhost:8080/v1/members 여기로 POST 요청을 보내고 await을 통해 서버 응답을 기다렸다가 registerResponse 에 저장
    const registerResponse = await apiClient.post("/members", data);
    return registerResponse.data; // .data를 써서 서버에서 반환한 회원가입 결과 정보에서 JSON 데이터만 뽑아옴
}
