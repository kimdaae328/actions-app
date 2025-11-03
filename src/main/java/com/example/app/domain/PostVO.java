package com.example.app.domain;

import com.example.app.audit.Period;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter @ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PostVO extends Period {
    private Long id;
    private String postTitle;
    private String postContent;
    private int postReadCount;
    private String postStatus;
    private Long memberId;
}
