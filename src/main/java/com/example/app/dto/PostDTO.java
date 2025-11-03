package com.example.app.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter @Setter @ToString
@EqualsAndHashCode(of = "id")
public class PostDTO {
    private Long id;
    private String postTitle;
    private String postContent;
    private int postReadCount;
    private String postStatus;
    private Long memberId;
    private String memberName;
    private String createdDateTime;
    private String updatedDateTime;
    private String createdDate;
    private String relativeDate;
    private List<PostFileDTO> postFiles;
}
