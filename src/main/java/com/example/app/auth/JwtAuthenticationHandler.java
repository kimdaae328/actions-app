package com.example.app.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("AuthenticationEntryPoint Exception: {}", authException.getMessage());
        if(request.getRequestURI().startsWith("/api/")){
//            REST 요청인 경우
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        }else{
//            일반 웹 요청인 경우
            response.sendRedirect("/member/login");
        }
    }
}
