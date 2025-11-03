package com.example.app.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class PostFileDTO {
    private Long id;
    private String postFileName;
    private String postFilePath;
    private String downloadUrl;
    private String postFileSize;
    private Long postId;
    private String createdDateTime;
    private String updatedDateTime;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // this 객체를 JSON 문자열로 변환
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return super.toString();
        }
    }
}
