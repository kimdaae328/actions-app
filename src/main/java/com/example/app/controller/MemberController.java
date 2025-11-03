package com.example.app.controller;

import com.example.app.aop.aspect.annotation.LogReturnStatus;
import com.example.app.aop.aspect.annotation.LogStatus;
import com.example.app.auth.CustomUserDetails;
import com.example.app.auth.JwtTokenProvider;
import com.example.app.dto.MemberDTO;
import com.example.app.enumeration.MemberRole;
import com.example.app.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/member/**")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse response;

//    회원가입
    @GetMapping("join")
    public String join(MemberDTO memberDTO, Model model) {
        model.addAttribute("memberDTO", memberDTO);
        return "member/join";
    }

    @GetMapping("sns/join")
    public String join(@CookieValue(value = "memberEmail", required = false) String memberEmail,
                       MemberDTO memberDTO, Model model) {
        memberDTO.setSnsEmail(memberEmail);
        model.addAttribute("memberDTO", memberDTO);
        return "member/sns/join";
    }

    @PostMapping("sns/join")
    public RedirectView join(@CookieValue(value = "role", required = false) String role,
                             @CookieValue(value = "provider", required = false) String provider, MemberDTO memberDTO) {
        memberDTO.setMemberRole(role.equals("ROLE_MEMBER") ? MemberRole.MEMBER : MemberRole.ADMIN);
        memberDTO.setProvider(provider);
        memberService.joinSns(memberDTO);

        jwtTokenProvider.createAccessToken(memberDTO.getSnsEmail(), provider);
        jwtTokenProvider.createRefreshToken(memberDTO.getSnsEmail(), provider);

        return new RedirectView("/post/list/1");
    }

    @PostMapping("join")
    public RedirectView join(MemberDTO memberDTO) {
        memberService.join(memberDTO);
        return new RedirectView("member/login");
    }

    //    로그인
    @GetMapping("login")
    public String login(MemberDTO memberDTO, Model model,
                        @CookieValue(value = "remember", required = false) boolean remember,
                        @CookieValue(value = "rememberEmail", required = false) String rememberEmail) {
        memberDTO.setMemberEmail(rememberEmail);
        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("remember", remember);
        return "member/login";
    }
}
