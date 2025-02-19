import React from "react";
import { logout } from "../components/LoginApi";

const Dashboard = () => {
    return (
        <div className="flex flex-col justify-center items-center min-h-screen">
            <h1 className="text-2xl font-bold">로그인 후 화면</h1>
            <button 
                onClick={logout}
                className="mt-4 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
            >
                로그아웃
            </button>
        </div>
    );
};

export default Dashboard;
