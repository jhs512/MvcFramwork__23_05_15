package com.ll.boundedContext.article.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Article {
    private long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String title;
    private String body;
    private boolean isBlind;
}