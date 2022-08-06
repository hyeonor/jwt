package com.inflearn.jwt.filter;

import javax.servlet.*;
import java.io.IOException;

public class MyThirdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("필터 3");
        // 필터가 끝나지 않고 계속 진행되게 하기 위해서
        chain.doFilter(request, response);
    }
}