package com.be.dohands.article.service;

import com.be.dohands.article.Article;
import com.be.dohands.article.dto.CreateArticleDto;
import com.be.dohands.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional
    public void saveArticle(CreateArticleDto dto) {
        Article article = new Article(dto.title(), dto.content());
        articleRepository.save(article);
    }
}
