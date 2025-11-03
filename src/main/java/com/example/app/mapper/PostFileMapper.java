package com.example.app.mapper;

import com.example.app.domain.PostFileVO;
import com.example.app.dto.PostFileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostFileMapper {
//    추가
    public void insertPostFile(PostFileVO postFileVO);

//    조회
    public List<PostFileDTO> selectPostFilesByPostId(Long postId);
    public Optional<PostFileDTO> selectPostFilePathByPostFileId(Long postFileId);

//    조회
    public Optional<PostFileDTO> selectPostFileById(Long id);

//    삭제
    public void deletePostFile(Long id);
}
