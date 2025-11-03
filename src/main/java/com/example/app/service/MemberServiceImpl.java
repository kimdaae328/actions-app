package com.example.app.service;

import com.example.app.common.exception.MemberLoginFailException;
import com.example.app.common.exception.MemberNotFoundException;
import com.example.app.dto.MemberDTO;
import com.example.app.repository.MemberDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberDAO memberDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void join(MemberDTO memberDTO) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberDAO.save(toVO(memberDTO));
    }

    @Override
    public void joinSns(MemberDTO memberDTO) {
        memberDAO.saveSns(toVO(memberDTO));
    }

    @Override
    public MemberDTO login(MemberDTO memberDTO) {
        return memberDAO.findForLogin(memberDTO).orElseThrow(MemberLoginFailException::new);
    }

    @Override
    @Cacheable(value="member", key="#memberEmail")
    public MemberDTO getMember(String memberEmail, String provider) {
        return (provider == null ? memberDAO.findByMemberEmail(memberEmail)
                : memberDAO.findBySnsEmail(memberEmail)).orElseThrow(MemberNotFoundException::new);
    }
}
