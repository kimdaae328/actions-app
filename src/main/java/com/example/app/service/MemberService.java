package com.example.app.service;

import com.example.app.domain.MemberVO;
import com.example.app.dto.MemberDTO;

public interface MemberService {
//    회원가입
    public void join(MemberDTO memberDTO);
    public void joinSns(MemberDTO memberDTO);

//    로그인
    public MemberDTO login(MemberDTO memberDTO);

//    회원 정보 조회
    public MemberDTO getMember(String memberEmail, String provider);
    
    default MemberVO toVO(MemberDTO memberDTO) {
        return MemberVO.builder()
                .id(memberDTO.getId())
                .memberName(memberDTO.getMemberName())
                .snsEmail(memberDTO.getSnsEmail())
                .provider(memberDTO.getProvider())
                .profileUrl(memberDTO.getProfileUrl())
                .memberEmail(memberDTO.getMemberEmail())
                .memberPassword(memberDTO.getMemberPassword())
                .memberStatus(memberDTO.getMemberStatus())
                .memberRole(memberDTO.getMemberRole())
                .createdDate(memberDTO.getCreatedDate())
                .updatedDate(memberDTO.getUpdatedDate())
                .build();
    }
}
