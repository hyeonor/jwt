package com.inflearn.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inflearn.jwt.config.auth.UserDetailsImpl;
import com.inflearn.jwt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프인 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
// login 요청해서 username, password 전송하면 (POST)
// UsernamePasswordAuthenticationFilter 동작
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도 중");


        try {
            // 1. username, password 받아서
            // json 데이터를 파싱해줌
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            // 1-2. 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new  UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // 2. 정상인지 로그인 시도 해봄. authenticationManager로 로그인 시도를 하면
            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행 후 정상이면 authentication이 리턴됨
            // DB에 있는 username과 password가 일치한다는 것
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. PrincipalDetails를 세션에 담고 (권한 관리 위해. 권한 1개뿐이라면 필요없음) => 로그인이 되었다는 뜻
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
            System.out.println("로그인 완료됨 " + userDetailsImpl.getUser().getUsername());
            System.out.println("================1================");

            // authentication 객체가 session 영역에 저장해야하고 그 방법이 return 해줌
            // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것
            // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없음. 근데 단지 권한 처리때문에 session에 넣어 줌
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("================2================");

        return null;
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증 완료");
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authResult.getPrincipal();

        //RSA 방식은 아니고 Hash 암호방식
        String jwtToken = JWT.create()
                .withSubject("cos 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) // 만료시간
                .withClaim("id", userDetailsImpl.getUser().getId())
                .withClaim("username", userDetailsImpl.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
