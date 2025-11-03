package com.example.app.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {
    private int page;
    private int pageCount;
    private int startPage;
    private int endPage;
    private int rowCount;
    private int offset;
    private int realEnd;
    private boolean hasNextGroup, hasPreviousGroup;

    public Criteria(int page, int total) {
        rowCount = 10;
        pageCount = 10;
        this.page = Math.max(1, page);
        endPage = (int)(Math.ceil(page / (double)pageCount)) * pageCount;
        startPage = endPage - pageCount + 1;
        realEnd = (int)(Math.ceil(total / (double)rowCount));
        endPage = Math.min(realEnd, endPage);
        endPage = Math.max(1, this.endPage);
        offset = (page - 1) * rowCount;
        hasNextGroup = endPage < realEnd;
        hasPreviousGroup = startPage > 1;
    }

}

