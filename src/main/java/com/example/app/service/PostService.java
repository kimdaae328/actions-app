package com.example.app.service;

import com.example.app.common.search.Search;
import com.example.app.domain.PostFileVO;
import com.example.app.domain.PostVO;
import com.example.app.dto.PostDTO;
import com.example.app.dto.PostFileDTO;
import com.example.app.dto.PostsCriteriaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
//    목록
    public PostsCriteriaDTO list(int page, Search search);
//    조회
    public PostDTO getPost(Long id);
    public void setPreSignedUrl(PostDTO postDTO);
//    추가
    public void write(PostDTO postDTO, List<MultipartFile> multipartFiles);
//    삭제
    public void delete(Long id);
//    수정
    public PostDTO update(PostDTO postDTO, Long[] deleteFiles, List<MultipartFile> files);

    default PostVO toVO(PostDTO postDTO){
        return PostVO.builder()
                .id(postDTO.getId())
                .postTitle(postDTO.getPostTitle())
                .postContent(postDTO.getPostContent())
                .postReadCount(postDTO.getPostReadCount())
                .postStatus(postDTO.getPostStatus())
                .memberId(postDTO.getMemberId())
                .build();
    }

    default PostFileVO toVO(PostFileDTO postFileDTO){
        return PostFileVO.builder()
                .id(postFileDTO.getId())
                .postFileName(postFileDTO.getPostFileName())
                .postFilePath(postFileDTO.getPostFilePath())
                .postFileSize(postFileDTO.getPostFileSize())
                .postId(postFileDTO.getPostId())
                .createdDate(postFileDTO.getCreatedDateTime())
                .updatedDate(postFileDTO.getUpdatedDateTime())
                .build();
    }
}
