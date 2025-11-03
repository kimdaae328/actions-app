package com.example.app.audit;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter @ToString
@SuperBuilder
public abstract class Period {
    private String createdDate;
    private String updatedDate;
}
