import React, { useState } from "react";
import * as loginApi from "../components/LoginApi"; // 로그인 API
import { useMutation } from "@tanstack/react-query";

const Login = () => {
    const [form, setForm] = useState({
        username: "",
        password: ""
    });

    // 입력값 변경 핸들러
    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    // 로그인 요청을 위한 mutation
    const loginMutate = useMutation({
        mutationFn: loginApi.login,
        onSuccess: (response) => {
            console.log("로그인 성공", response);
            console.log("Headers:", response.headers);
            alert("로그인 성공!");
        },
        onError: (error) => {
            console.error("로그인 실패", error);
            alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
        }
    });

    // 로그인 폼 제출 핸들러
    const submitHandler = (e) => {
        e.preventDefault(); // 기본 폼 제출 이벤트 방지
        loginMutate.mutate(form); // 로그인 API 요청 실행
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <form 
                onSubmit={submitHandler} 
                className="bg-white shadow-lg rounded-lg p-6 w-96"
            >
                <h2 className="text-2xl font-bold text-center mb-6">로그인</h2>
                <input 
                    name="username" type="email" placeholder="이메일"
                    value={form.username} onChange={handleChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                    required
                />
                <input 
                    name="password" type="password" placeholder="비밀번호"
                    value={form.password} onChange={handleChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                    required
                />
                <button 
                    type="submit" disabled={loginMutate.isLoading}
                    className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition"
                >
                    {loginMutate.isLoading ? "로그인 중..." : "로그인"}
                </button>
            </form>
        </div>
    );
};

export default Login;

