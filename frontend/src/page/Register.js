import React from "react";
import * as registerApi from "../components/RegisterApi";
import { useMutation } from "@tanstack/react-query";

const Register = () => {
    const registerMutate = useMutation({
        // mutationFn은 실제로 실행할 API 요청 함수
        mutationFn: registerApi.register, // registerApi.register 는 회원가입을 처리하는 API 요청 함수
        onSuccess: (response) => { // 회원가입 요청이 성공적으로 처리됐을때 실행되는 콜백 함수
            console.log("회원가입 성공", response.data);
            alert("회원가입 됐음");
        },
        onError: (error) => { // 회원가입 요청 실패했을 떄 실행할 콜백 함수
            console.error("회원가입 실패", error);
            alert("회원가입 실패용");
        }
    });

    // form 태그의 onSubmit 이벤트를 처리하는 함수, 맨 하단 주석 참고
    const submitHandler = (e) => { // e는 event 객체를 의미 , e.target을 통해 폼 요소 접근 가능
        /*
            form이 기본적으로 새로고침 되는걸 막음. 기본적으로 <form>을 제출하면 페이지가 새로고침 되면서 입력한 데이터가 사라짐
            이를 방지하기 위해서 비동기 요청으로 회원가입을 처리할 수 있도록 설정
        */
        e.preventDefault();
        const formData = new FormData(e.target); // e.target 은 이벤트가 발생한 <form> 요소를 가르킴, new FormData를 통해서 폼 내부의 입력값 가져옴
        const data = Object.fromEntries(formData); // Object.fromEntries(formData) 를 통해 FormData 객체를 일반 JavaScript 객체로 변환함
        // 이렇게 변환하면 서버로 쉽게 전송할 수 있는 JSON 형식의 데이터가 됨

        registerMutate.mutate(data); // 이 data를 담아서 서버로 회원가입 요청 보냄, mutate(data)를 실행하면 registerApi.register(data)를 통해서
        // 서버 단에 /v1/members로 POST 요청 보냄냄
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <form 
                onSubmit={submitHandler} // submitHandler 사용
                className="bg-white shadow-lg rounded-lg p-6 w-96"
            >
                <h2 className="text-2xl font-bold text-center mb-6">회원가입</h2>
                <input 
                    name="email" type="email" placeholder="이메일"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />
                <input 
                    name="name" type="text" placeholder="이름"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />
                <input 
                    name="phone" type="text" placeholder="전화번호"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />
                <input 
                    name="password" type="password" placeholder="비밀번호"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-3"
                />
                <button 
                    type="submit" disabled={registerMutate.isLoading}
                    className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition"
                >
                    {registerMutate.isLoading ? "회원가입 중..." : "회원가입"}
                </button>
            </form>
        </div>
    );
}

export default Register;


/*
    예시로
    <form onSubmit={submitHandler}>
        <input type="email" name="email" />
        <input type="text" name="name" />
        <input type="text" name="phone" />
        <input type="password" name="password" />
        <button type="submit">회원가입</button>
    </form>
    이런식의 form html을 new FormData를 써서
    FormData {
        "email": "test@example.com",
        "name": "홍길동",
        "phone": "010-1234-5678",
        "password": "securePassword"
    }
    이렇게 변경된다는 것
*/