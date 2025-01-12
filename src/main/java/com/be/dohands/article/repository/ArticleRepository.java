package com.be.dohands.article.repository;

import com.be.dohands.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findBySheetRow(Integer sheetRow);
}
