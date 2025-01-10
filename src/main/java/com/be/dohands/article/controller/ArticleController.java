package com.be.dohands.article.controller;

import com.be.dohands.article.dto.ArticleSlice;
import com.be.dohands.article.dto.CreateArticleDto;
import com.be.dohands.article.service.ArticleService;
import com.be.dohands.common.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/articles")
    public ResponseEntity<ArticleSlice> getArticles(@RequestParam String next, @RequestParam int size,
                                                    @AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        return ResponseEntity.ok(articleService.findArticles(loginId, next, size));
    }

    @PostMapping("/articles/{articleId}")
    public ResponseEntity<Void> readArticle(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        articleService.readArticle(loginId, articleId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
