import { useEffect, useState } from "react";

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        setIsAuthenticated(!!token); // 토큰이 있으면 true, 없으면 false
    }, []);

    return isAuthenticated;
};
