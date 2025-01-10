package com.be.dohands.article.dto;

import lombok.Getter;

@Getter
public class ArticleDto {
    private Long articleId;
    private String title;
    private String content;
    private boolean isRead;

    public ArticleDto(Long articleId, String title, String content, boolean isRead) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }

    public void updateRead() {
        this.isRead = true;
    }
}
