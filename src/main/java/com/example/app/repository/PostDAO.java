package com.example.app.repository;

import com.example.app.common.pagination.Criteria;
import com.example.app.common.search.Search;
import com.example.app.domain.PostVO;
import com.example.app.dto.PostDTO;
import com.example.app.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDAO {
    private final PostMapper postMapper;

//    목록
    public List<PostDTO> findAll(Criteria criteria, Search search){
        return postMapper.selectPosts(criteria, search);
    }

//    전체 개수
    public int findCountAll(Search search) {
        return postMapper.selectPostCount(search);
    }

//    조회
    public Optional<PostDTO> findById(Long id) {
        return postMapper.selectPost(id);
    }

//    추가
    public void save(PostDTO postDTO) {postMapper.insert(postDTO);}

//    조회수 증가
    public void increaseReadCount(Long id) {
        postMapper.updateReadCount(id);
    }

//    삭제
    public void delete(Long id) {
        postMapper.delete(id);
    }

//    수정
    public void update(PostVO postVO) {postMapper.update(postVO);}
}
