package com.example.app.dto;

import com.example.app.common.pagination.Criteria;
import lombok.*;

import java.util.List;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostsCriteriaDTO {
    private List<PostDTO> posts;
    private Criteria criteria;
}
