package com.inflearn.jwt.config;

import com.inflearn.jwt.filter.MyFirstFilter;
import com.inflearn.jwt.filter.MySecondFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFirstFilter> firstFilter() {
        FilterRegistrationBean<MyFirstFilter> bean = new FilterRegistrationBean<>(new MyFirstFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1); // 낮은 번호가 필터 중에서 가장 먼저 실행됨.
        return bean;
    }

    @Bean
    public FilterRegistrationBean<MySecondFilter> secondFilter() {
        FilterRegistrationBean<MySecondFilter> bean = new FilterRegistrationBean<>(new MySecondFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(0); // 낮은 번호가 필터 중에서 가장 먼저 실행됨.
        return bean;
    }
}
