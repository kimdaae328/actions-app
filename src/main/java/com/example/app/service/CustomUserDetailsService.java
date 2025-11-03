package com.example.app.service;

import com.example.app.auth.CustomUserDetails;
import com.example.app.dto.MemberDTO;
import com.example.app.repository.MemberDAO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberDAO memberDAO;
    private final HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String provider = null;
        MemberDTO memberDTO = null;
        log.info("##########{}", username);
        if(request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if("provider".equals(cookie.getName())){
                    provider = cookie.getValue();
                }
            }
        }
        if(provider == null){
//        이메일로 전체 정보 조회
            memberDTO = memberDAO.findByMemberEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("소유자를 찾을 수 없습니다."));
        }else{
            memberDTO = memberDAO.findBySnsEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("소유자를 찾을 수 없습니다."));
        }

        return new CustomUserDetails(memberDTO);
    }
}















