import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Register from "./page/Register";
import Login from "./page/Login";
import Logout from "./page/Logout";

function App() {
    return (
        <Router>
            <div className="flex justify-between items-center p-4 bg-gray-200">
                <Link to="/login" className="mr-4">로그인</Link>
                <Link to="/register" className="mr-4">회원가입</Link>
                <Logout /> {/* 로그아웃 버튼 */}
            </div>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
            </Routes>
        </Router>
    );
}

export default App;

