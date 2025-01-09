package com.be.dohands.article.controller;

import com.be.dohands.article.dto.CreateArticleDto;
import com.be.dohands.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/admin/articles")
    public ResponseEntity<Void> createArticle(@RequestBody CreateArticleDto createArticleDto) {
        articleService.saveArticle(createArticleDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
