package com.example.app.domain;

import com.example.app.audit.Period;
import com.example.app.enumeration.MemberRole;
import com.example.app.enumeration.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@SuperBuilder
public class MemberVO extends Period {
    private Long id;
    private String memberName;
    private String memberEmail;
    private String memberPassword;
    private Status memberStatus;
    private MemberRole memberRole;
    private String snsEmail;
    private String provider;
    private String profileUrl;
}