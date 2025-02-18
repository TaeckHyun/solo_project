import React from "react";
import * as MemberApi from "../components/MemberApi";
import { useMutation } from "@tanstack/react-query";

const Register = () => {
    const registerMutate = useMutation({
        mutationFn: MemberApi.register,
        onSuccess: (response) => {
            console.log("회원가입 성공", response.data);
            alert("회원가입 됐음");
        },
        onError: (error) => {
            console.error("회원가입 실패", error);
            alert("회원가입 실패함ㅋ");
        }
    });

    const submitHandler = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData);

        registerMutate.mutate(data);
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <form 
                onSubmit={submitHandler} 
                className="bg-white shadow-lg rounded-lg p-6 w-96"
            >
                <h2 className="text-2xl font-bold text-center mb-6">회원가입</h2>

                <input 
                    name="email"
                    type="email"
                    placeholder="이메일"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />

                <input 
                    name="name"
                    type="text"
                    placeholder="이름"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />

                <input 
                    name="phone"
                    type="text"
                    placeholder="전화번호"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />

                <input 
                    name="password"
                    type="password"
                    placeholder="비밀번호"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />

                <button 
                    type="submit" 
                    disabled={registerMutate.isLoading}
                    className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition"
                >
                    {registerMutate.isLoading ? "회원가입 중..." : "회원가입"}
                </button>
            </form>
        </div>
    );
}

export default Register;
