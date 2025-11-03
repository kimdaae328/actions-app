package com.example.app.domain;

import com.example.app.audit.Period;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter @ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PostFileVO extends Period {
    private Long id;
    private String postFileName;
    private String postFilePath;
    private String postFileSize;
    private Long postId;
}
