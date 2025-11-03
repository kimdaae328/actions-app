package com.example.app.mapper;

import com.example.app.domain.MemberVO;
import com.example.app.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {
//    회원가입
    public void insert(MemberVO memberVO);
    public void insertSns(MemberVO memberVO);
//    로그인
    public Optional<MemberDTO> selectForLogin(MemberDTO memberDTO);
//    이메일로 조회
    public Optional<MemberDTO> selectMemberByMemberEmail(String memberEmail);
    public Optional<MemberDTO> selectMemberBySnsEmail(String snsEmail);
}
