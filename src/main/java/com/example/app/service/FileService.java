package com.example.app.service;

import com.example.app.dto.PostFileDTO;
import com.example.app.repository.PostFileDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {
    private final PostFileDAO postFileDAO;

//    조회
    public Optional<PostFileDTO> getPostFile(Long id){
        return postFileDAO.findById(id);
    }
}
