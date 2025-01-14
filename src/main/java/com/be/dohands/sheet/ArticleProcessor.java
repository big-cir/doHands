package com.be.dohands.sheet;


import com.be.dohands.article.Article;
import com.be.dohands.article.Article.ArticleBuilder;
import com.be.dohands.article.repository.ArticleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleProcessor extends SheetProcessor<Article> {

    private final ArticleRepository articleRepository;

    @Override
    protected TransformResult<Article> transformRow(List<Object> rows, Integer sheetRow) {

        Optional<Article> articleOptional = articleRepository.findBySheetRow(sheetRow);

        ArticleBuilder articleBuilder = Article.builder()
            .title(rows.get(0).toString())
            .content(rows.get(1).toString())
            .sheetRow(sheetRow);

        articleOptional.ifPresent(existMember -> articleBuilder.articleId(existMember.getArticleId()));

        Article article = articleBuilder.build();

        Article savedArticle = articleRepository.save(article);

        return TransformResult.of(savedArticle, false);

    }

    @Override
    protected Article saveEntity(Article entity) {

        return articleRepository.save(entity);
    }
}
