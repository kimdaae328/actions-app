package com.example.app.common.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter @ToString
public class Search {
    private String category;
    private String keyword;
}
