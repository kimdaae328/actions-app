package com.example.app.controller;

import com.example.app.aop.aspect.annotation.LogReturnStatus;
import com.example.app.auth.CustomUserDetails;
import com.example.app.auth.JwtTokenProvider;
import com.example.app.dto.MemberDTO;
import com.example.app.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/auth/**")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs{
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse response;
    private final MemberService memberService;
    private final RedisTemplate<String, Object> redisTemplate;

//    로그인
    @PostMapping("login")
    @LogReturnStatus
    public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO){
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(memberDTO.getMemberEmail(), memberDTO.getMemberPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtTokenProvider.createAccessToken(((CustomUserDetails) authentication.getPrincipal()).getMemberEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(((CustomUserDetails) authentication.getPrincipal()).getMemberEmail());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            Cookie rememberEmailCookie = new Cookie("rememberEmail", memberDTO.getMemberEmail());
            Cookie rememberCookie = new Cookie("remember", String.valueOf(memberDTO.isRemember()));

            rememberEmailCookie.setPath("/"); // 모든 경로에서 접근 가능
            rememberCookie.setPath("/"); // 모든 경로에서 접근 가능

            if (memberDTO.isRemember()) {
                // 예: 로그인 이메일을 쿠키에 저장 (민감정보는 피해야 함)
                rememberEmailCookie.setMaxAge(60 * 60 * 24 * 30); // 30일 유지
                response.addCookie(rememberEmailCookie);

                rememberCookie.setMaxAge(60 * 60 * 24 * 30); // 30일 유지
                response.addCookie(rememberCookie);
            } else {
                // 쿠키 삭제
                rememberEmailCookie.setMaxAge(0); // 삭제
                response.addCookie(rememberEmailCookie);

                rememberCookie.setMaxAge(0); // 삭제
                response.addCookie(rememberCookie);
            }
            return ResponseEntity.ok(tokens);

        } catch(AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 실패: " + e.getMessage()));
        }
    }

//    로그아웃
    @PostMapping("logout")
    public void logout(@CookieValue(value = "accessToken", required = false) String token) {
        String username = jwtTokenProvider.getUserName(token);
        String provider = (String) jwtTokenProvider.getClaims(token).get("provider");
        if(provider == null){
            jwtTokenProvider.deleteRefreshToken(username);
            jwtTokenProvider.addToBlacklist(token);
        }else{
            jwtTokenProvider.deleteRefreshToken(username, provider);
            jwtTokenProvider.addToBlacklist(token);
        }

        Cookie deleteAccessCookie = new Cookie("accessToken", null);
        deleteAccessCookie.setHttpOnly(true);
        deleteAccessCookie.setSecure(false);
        deleteAccessCookie.setPath("/");
        deleteAccessCookie.setMaxAge(0);

        response.addCookie(deleteAccessCookie);

        Cookie deleteRefreshCookie = new Cookie("refreshToken", null);
        deleteRefreshCookie.setHttpOnly(true);
        deleteRefreshCookie.setSecure(false);
        deleteRefreshCookie.setPath("/");
        deleteRefreshCookie.setMaxAge(0);

        response.addCookie(deleteRefreshCookie);

        Cookie memberEmailCookie = new Cookie("memberEmail", null);
        memberEmailCookie.setHttpOnly(true);
        memberEmailCookie.setSecure(false);
        memberEmailCookie.setPath("/");
        memberEmailCookie.setMaxAge(0);

        response.addCookie(memberEmailCookie);


        Cookie roleCookie = new Cookie("role", null);
        roleCookie.setHttpOnly(true);
        roleCookie.setSecure(false);
        roleCookie.setPath("/");
        roleCookie.setMaxAge(0);

        response.addCookie(roleCookie);

        Cookie providerCookie = new Cookie("provider", null);
        providerCookie.setHttpOnly(true);
        providerCookie.setSecure(false);
        providerCookie.setPath("/");
        providerCookie.setMaxAge(0);

        response.addCookie(providerCookie);

        redisTemplate.delete("member::" + username);
        Set<String> keys = redisTemplate.keys("posts::post_*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

//    리프레시 토큰으로 엑세스 토큰 발급
    @GetMapping("refresh")
    public Map<String, String> refresh(@CookieValue(value = "refreshToken", required = false) String token){
        String username = jwtTokenProvider.getUserName(token);
        String refreshToken = jwtTokenProvider.getRefreshToken(username);
        if(refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)){
            throw new RuntimeException("리프레시 토큰이 유효하지 않습니다.");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) jwtTokenProvider.getAuthentication(refreshToken).getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(customUserDetails.getMemberEmail());

        jwtTokenProvider.deleteRefreshToken(username);
        jwtTokenProvider.createRefreshToken(customUserDetails.getMemberEmail());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);

        return tokenMap;
    }

    @GetMapping("/info")
    public MemberDTO getMyInfo(HttpServletRequest request) {
        String token = jwtTokenProvider.parseTokenFromHeader(request);
        if (token == null) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        // 블랙리스트 체크 추가
        if (jwtTokenProvider.isTokenBlackList(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다.");
        }

        String memberEmail = jwtTokenProvider.getUserName(token);
        String provider = (String) jwtTokenProvider.getClaims(token).get("provider");
        MemberDTO member = memberService.getMember(memberEmail, provider);

        return member;
    }
}












