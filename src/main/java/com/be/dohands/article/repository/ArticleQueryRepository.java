package com.be.dohands.article.repository;

import static com.be.dohands.article.QArticle.article;

import com.be.dohands.article.Article;
import com.be.dohands.article.dto.ArticleDto;
import com.be.dohands.article.dto.ArticleSlice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleQueryRepository {

    private final JPAQueryFactory factory;

    public ArticleSlice findAllArticles(Long next, int size) {
        List<Article> articles = factory.selectFrom(article)
                .where(ltNext(next))
                .orderBy(article.articleId.desc())
                .limit(size)
                .fetch();

        return sliceResult(size, articles);
    }

    private ArticleSlice sliceResult(int size, List<Article> articles) {
        boolean hasNext = false;
        Long next = null;
        List<ArticleDto> list = null;
        if (articles != null && articles.size() >= size) {
            next = articles.get(articles.size() - 1).getArticleId();
            hasNext = true;
        }

        list = articles.stream()
                .map(a -> new ArticleDto(
                        a.getArticleId(), a.getTitle(), a.getContent(), false
                ))
                .collect(Collectors.toList());

        return new ArticleSlice(list, next, hasNext);
    }

    private BooleanExpression ltNext(Long next) {
        if (next == null) {
            return null;
        }
        return article.articleId.lt(next);
    }
}
