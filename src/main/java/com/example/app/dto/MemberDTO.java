package com.example.app.dto;

import com.example.app.audit.Period;
import com.example.app.auth.CustomUserDetails;
import com.example.app.enumeration.MemberRole;
import com.example.app.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

@Component
@Getter @Setter @ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class MemberDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String memberName;
    private String memberEmail;
    private String memberPassword;
    private Status memberStatus;
    private MemberRole memberRole;
    private String createdDate;
    private String updatedDate;
    private boolean remember;
    private String snsEmail;
    private String provider;
    private String profileUrl;

    //    엑세스 토큰에 담을 정보
    public MemberDTO(CustomUserDetails userDetails){
        this.id = userDetails.getId();
        this.memberName = userDetails.getMemberName();
        this.memberEmail = userDetails.getMemberEmail();
        this.memberStatus = userDetails.getMemberStatus();
        this.memberRole = userDetails.getMemberRole();
        this.createdDate = userDetails.getCreatedDate();
        this.updatedDate = userDetails.getUpdatedDate();
    }

    public MemberRole getMemberRole() {
        return memberRole != null ? memberRole : MemberRole.MEMBER;
    }
}