package com.inflearn.jwt.config;

import com.inflearn.jwt.config.jwt.JwtAuthenticationFilter;
import com.inflearn.jwt.filter.MyThirdFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration // ioc 할 수 있게 만들어 줌
@EnableWebSecurity // security 활성화
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 스프링 시큐리티가 실행되기 이전에 실햄됨
        http.addFilterBefore(new MyThirdFilter(), SecurityContextPersistenceFilter.class);
        // csrf 토큰 비활성화 (테스트 시 걸어두는 게 좋음)
        http.csrf().disable();
        // STATELESS: 세션을 사용하지 않겠다는 의미
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // @CrossOrigin(인증 없을 때),시큐리티 필터에 등록 (인증 있을 때)
                // corsFilter 를 거쳐야 모든 요청이 일어남
                .addFilter(corsFilter)
                // 폼 로그인 사용 X
                .formLogin().disable()
                // 기본적인 http 방식을 사용 X
                .httpBasic().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }
}
