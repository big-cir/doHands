package com.be.dohands.article.dto;

import com.be.dohands.article.Article;
import java.util.List;
import lombok.Getter;

@Getter
public class ArticleSlice {
    private List<ArticleDto> items;
    private Long next;
    private boolean hasNext;

    public ArticleSlice(List<ArticleDto> items, Long next, boolean hasNext) {
        this.items = items;
        this.next = next;
        this.hasNext = hasNext;
    }
}
