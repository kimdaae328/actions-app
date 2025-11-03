package com.example.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadPostFile(MultipartFile file, String path) throws IOException {
        String fileName = getFileName(file, path);
//        S3 업로드 요청 객체
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

//        S3에 파일 업로드
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return fileName;
    }
    
    public String getPreSignedUrl(String key, Duration validDuration) {
//        S3에서 조회할 객체 요청 생성
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(validDuration)
                .getObjectRequest(request)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String getPreSignedDownloadUrl(String key, String originalName, Duration validDuration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
//                브라우저가 파일을 다운로드하도록 Response Header 설정
                .responseContentDisposition("attachment; filename=\"" + originalName + "\"")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(validDuration)
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String getFileName(MultipartFile file, String path){
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if(originalFileName != null && originalFileName.contains(".")){
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return path + "/" + UUID.randomUUID() + extension;
    }
}









