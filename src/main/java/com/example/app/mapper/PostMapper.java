package com.example.app.mapper;

import com.example.app.common.pagination.Criteria;
import com.example.app.common.search.Search;
import com.example.app.domain.PostVO;
import com.example.app.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {
//    목록
    public List<PostDTO> selectPosts(@Param("criteria") Criteria criteria, @Param("search") Search search);
//    전체 개수
    public int selectPostCount(@Param("search") Search search);
//    조회
    public Optional<PostDTO> selectPost(Long id);
//    추가
    public void insert(PostDTO postDTO);
//    조회수 증가
    public void updateReadCount(Long id);
//    삭제
    public void delete(Long id);
//    수정
    public void update(PostVO postVO);
}
