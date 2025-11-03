package com.example.app.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = oAuth2User.getAttribute("provider");
        String email = oAuth2User.getAttribute("email");
        boolean isExist = oAuth2User.getAttribute("exist");
        String role = oAuth2User.getAuthorities().stream().findFirst().orElseThrow(IllegalAccessError::new).getAuthority();
        String path = null;
        log.info("존재: {}", isExist);
        if(isExist){
            jwtTokenProvider.createAccessToken(email, provider);
            jwtTokenProvider.createRefreshToken(email, provider);

            path = "/post/list/1";
        }else{
            Cookie memberEmailCookie = new Cookie("memberEmail", email);
            memberEmailCookie.setHttpOnly(true);     // JS에서 접근 불가 (XSS 방지)
            memberEmailCookie.setSecure(false);       // HTTPS 환경에서만 전송
            memberEmailCookie.setPath("/");          // 모든 경로에 쿠키 적용
            memberEmailCookie.setMaxAge(60 * 60);    // 1시간

            response.addCookie(memberEmailCookie);

            Cookie roleCookie = new Cookie("role", role);
            roleCookie.setHttpOnly(true);     // JS에서 접근 불가 (XSS 방지)
            roleCookie.setSecure(false);       // HTTPS 환경에서만 전송
            roleCookie.setPath("/");          // 모든 경로에 쿠키 적용
            roleCookie.setMaxAge(60 * 60);    // 1시간

            response.addCookie(roleCookie);

            path = "/member/sns/join";
        }

        Cookie providerCookie = new Cookie("provider", provider);
        providerCookie.setHttpOnly(true);     // JS에서 접근 불가 (XSS 방지)
        providerCookie.setSecure(false);       // HTTPS 환경에서만 전송
        providerCookie.setPath("/");          // 모든 경로에 쿠키 적용
        providerCookie.setMaxAge(60 * 60);    // 1시간

        response.addCookie(providerCookie);

        response.sendRedirect(path);
    }
}

















