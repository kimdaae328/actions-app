package com.example.app.controller;

import com.example.app.dto.PostFileDTO;
import com.example.app.service.FileService;
import com.example.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files/**")
public class FileController {
    private final FileService fileService;
    private final S3Service s3Service;

    @GetMapping("download/{id}")
    public ResponseEntity<String> getDownloadUrl(@PathVariable Long id) {
        PostFileDTO postFile = fileService.getPostFile(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String downloadUrl = s3Service.getPreSignedDownloadUrl(
                postFile.getPostFilePath(),
                postFile.getPostFileName(),
                Duration.ofMinutes(5) // 5분 유효
        );

        return ResponseEntity.ok(downloadUrl);
    }
}
