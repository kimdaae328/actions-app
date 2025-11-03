package com.example.app.service;

import com.example.app.common.exception.PostNotFoundException;
import com.example.app.common.pagination.Criteria;
import com.example.app.common.search.Search;
import com.example.app.dto.PostDTO;
import com.example.app.dto.PostFileDTO;
import com.example.app.dto.PostsCriteriaDTO;
import com.example.app.repository.PostDAO;
import com.example.app.repository.PostFileDAO;
import com.example.app.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostDAO postDAO;
    private final PostFileDAO postFileDAO;
    private final S3Service s3Service;
    private final PostFileDTO postFileDTO;

    @Override
    public PostsCriteriaDTO list(int page, Search search) {
        Criteria criteria = new Criteria(page, postDAO.findCountAll(search));
        List<PostDTO> posts = postDAO.findAll(criteria, search);
        posts.forEach(post -> {
            post.setRelativeDate(DateUtils.toRelativeTime(post.getCreatedDateTime().split("\\.")[0]));
            post.setCreatedDate(post.getCreatedDateTime().split(" ")[0]);
        });
        return new PostsCriteriaDTO(posts, criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Cacheable(value = "posts", key="'post_' + #id")
    public PostDTO getPost(Long id) {
        postDAO.increaseReadCount(id);

        PostDTO postDTO = postDAO.findById(id).orElseThrow(PostNotFoundException::new);
        postDTO.setRelativeDate(DateUtils.toRelativeTime(postDTO.getCreatedDateTime().split("\\.")[0]));
        postDTO.setCreatedDate(postDTO.getCreatedDateTime().split(" ")[0]);

        return postDTO;
    }

    @Override
    public void setPreSignedUrl(PostDTO postDTO) {
        List<PostFileDTO> postFiles = postFileDAO.findAllByPostId(postDTO.getId());
        postFiles.forEach((postFile) -> {
            postFile.setPostFilePath(s3Service.getPreSignedUrl(postFile.getPostFilePath(), Duration.ofMinutes(5)));
        });

        postDTO.setPostFiles(postFiles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void write(PostDTO postDTO, List<MultipartFile> multipartFiles) {
        postDAO.save(postDTO);
        multipartFiles.forEach((multipartFile) -> {
            if(multipartFile.getOriginalFilename().equals("")){
                return;
            }

            PostFileDTO postFileDTO = new PostFileDTO();
            try {
                String s3Key = s3Service.uploadPostFile(multipartFile, getPath());

                postFileDTO.setPostId(postDTO.getId());
                postFileDTO.setPostFileName(multipartFile.getOriginalFilename());
                postFileDTO.setPostFilePath(s3Key);
                postFileDTO.setPostFileSize(String.valueOf(multipartFile.getSize()));

                postFileDAO.save(toVO(postFileDTO));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value="posts", key = "'post_' + #id")
    public void delete(Long id) {
        List<PostFileDTO> postFiles = postFileDAO.findAllByPostId(id);
        postFiles.forEach((postFile) -> {
            s3Service.deleteFile(postFile.getPostFilePath());
            postFileDAO.deleteById(postFile.getId());
        });

        postDAO.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = "posts", key = "'post_' + #postDTO.id")
    public PostDTO update(PostDTO postDTO, Long[] deleteFileIds, List<MultipartFile> multipartFiles) {
        postDAO.update(toVO(postDTO));

        if(deleteFileIds != null){
            Arrays.stream(deleteFileIds).forEach((id) -> {
                PostFileDTO postFile =
                        postFileDAO.findPostFilePathByPostFileId(id)
                                .orElseThrow(PostNotFoundException::new);
                s3Service.deleteFile(postFile.getPostFilePath());
                postFileDAO.deleteById(id);
            });
        }

        multipartFiles.forEach((multipartFile) -> {
            if(multipartFile.getOriginalFilename().equals("")){
                return;
            }

            PostFileDTO postFileDTO = new PostFileDTO();
            try {
                String s3Key = s3Service.uploadPostFile(multipartFile, getPath());

                postFileDTO.setPostId(postDTO.getId());
                postFileDTO.setPostFileName(multipartFile.getOriginalFilename());
                postFileDTO.setPostFilePath(s3Key);
                postFileDTO.setPostFileSize(String.valueOf(multipartFile.getSize()));

                postFileDAO.save(toVO(postFileDTO));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return postDTO;
    }

    public String getPath() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return today.format(formatter);
    }
}
