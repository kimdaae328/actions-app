package com.example.app.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthorizationHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("AccessDeniedException: {}", accessDeniedException.getMessage());

        if(request.getRequestURI().startsWith("/api/")){
//            REST 요청인 경우
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: Access Denied");

        }else{
//            일반 웹 요청인 경우
            response.sendRedirect("/member/login");
        }
    }
}
