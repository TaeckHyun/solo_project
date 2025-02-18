import React from "react";
import * as LoginApi from "../components/LoginApi";
import { useMutation } from "@tanstack/react-query";

const Login = () => {
    const loginMutate = useMutation({
        mutationFn: LoginApi.login,
        onSuccess: (response) => {
            console.log("로그인 성공", response.data);
            alert("로그인 성공함");
            window.location.href = "/dashboard"; // 로그인 후 이동할 페이지
        },
        onError: (error) => {
            console.log("로그인 실패", error);
            alert("로그인 실패함 ㅋㅋ");
        }
    });

    const submitHandler = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData);

        loginMutate.mutate(data);
    };

    return (
        <form onSubmit={submitHandler} className="flex flex-col gap-4 p-4 border rounded-md shadow-md w-96 mx-auto mt-20">
            <h2 className="text-xl font-bold text-center">로그인</h2>

            <input name="email" type="email" placeholder="이메일" className="p-2 border rounded"/>
            <input name="password" type="password" placeholder="비밀번호" className="p-2 border rounded"/>
            
            <button type="submit" className="p-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400"
                disabled={loginMutate.isLoading}>
                {loginMutate.isLoading ? "로그인 중..." : "로그인"}
            </button>
        </form>
    );
};

export default Login;
