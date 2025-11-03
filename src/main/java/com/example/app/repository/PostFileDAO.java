package com.example.app.repository;

import com.example.app.domain.PostFileVO;
import com.example.app.dto.PostFileDTO;
import com.example.app.mapper.PostFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostFileDAO {
    private final PostFileMapper postFileMapper;

//    추가
    public void save(PostFileVO postFileVO){
        postFileMapper.insertPostFile(postFileVO);
    }

//    조회
    public List<PostFileDTO> findAllByPostId(Long postId){
        return postFileMapper.selectPostFilesByPostId(postId);
    }
    public Optional<PostFileDTO> findPostFilePathByPostFileId(Long postFileId){
        return postFileMapper.selectPostFilePathByPostFileId(postFileId);
    }

//    조회
    public Optional<PostFileDTO> findById(Long id){
        return postFileMapper.selectPostFileById(id);
    }

//    삭제
    public void deleteById(Long id){
        postFileMapper.deletePostFile(id);
    }
}
