package com.inflearn.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyThirdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //이런식으로 메소드 확인 가능
        System.out.println(req.getMethod());

        // 토큰: cos를 만들어 줘야 함. ID, PW가 정상적으로 들어와서 로그인이 완료 되면 토큰을 만들어주고 그걸 응답
        // 요청할 때 마다 header에 Authorization에 value 값으로 토큰을 가지고 온다
        // 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증만 하면 된다 (RSA, HS256)
        if(req.getMethod().equals("POST")) {
            System.out.println("POST 요청됨");
            String headerAuth = req.getHeader("Authorization");
            System.out.println(headerAuth);
            System.out.println("필터 3");

            if(headerAuth.equals("cos")) {
                chain.doFilter(req, res);
            }else {
                res.setCharacterEncoding("UTF-8");
                PrintWriter out = res.getWriter();
                out.println("인증 안 됨");
            }
        }
    }
}