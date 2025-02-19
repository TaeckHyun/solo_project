import React from "react";
import { useMutation } from "@tanstack/react-query";
import * as logoutApi from "../components/LogoutApi"; // 로그아웃 API
import { useNavigate } from "react-router-dom"; // 페이지 이동을 위한 Hook

const Logout = () => {
    const navigate = useNavigate(); // 페이지 이동을 위한 Hook

    // 로그아웃 API 요청을 위한 mutation
    const logoutMutate = useMutation({
        mutationFn: logoutApi.logout,
        onSuccess: () => {
            console.log("로그아웃 성공!");
            alert("로그아웃 되었습니다.");
            navigate("/login"); // 로그아웃 후 로그인 페이지로 이동
        },
        onError: (error) => {
            console.error("로그아웃 실패", error);
            alert("로그아웃 실패! 다시 시도하세요.");
        }
    });

    return (
        <button
            onClick={() => logoutMutate.mutate()} // 로그아웃 실행
            className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
        >
            로그아웃
        </button>
    );
};

export default Logout;
