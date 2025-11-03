package com.example.app.auth;

import com.example.app.dto.MemberDTO;
import com.example.app.enumeration.MemberRole;
import com.example.app.enumeration.Status;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// Spring Security에서는 기본으로 최소 정보만 제공한다.
// 추가 정보를 토큰에 담고 싶다면 CustomUserDetails를 구성하여 활용한다.
@Getter
@ToString
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String memberName;
    private String memberEmail;
    private String snsEmail;
    private String memberPassword;
    private Status memberStatus;
    private MemberRole memberRole;
    private String createdDate;
    private String updatedDate;

    public CustomUserDetails(MemberDTO memberDTO) {
        this.id = memberDTO.getId();
        this.memberName = memberDTO.getMemberName();
        this.memberEmail = memberDTO.getMemberEmail();
        this.snsEmail = memberDTO.getSnsEmail();
        this.memberPassword = memberDTO.getMemberPassword();
        this.memberStatus = memberDTO.getMemberStatus();
        this.memberRole = memberDTO.getMemberRole();
        this.createdDate = memberDTO.getCreatedDate();
        this.updatedDate = memberDTO.getUpdatedDate();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return memberRole.getAuthorities();
    }

    @Override
    public String getPassword() {
        return memberPassword;
    }

    @Override
    public String getUsername() {
        return memberName;
    }
}
