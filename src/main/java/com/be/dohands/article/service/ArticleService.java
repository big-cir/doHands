package com.be.dohands.article.service;

import static com.be.dohands.notification.data.NotificationType.ARTICLE;

import com.be.dohands.article.Article;
import com.be.dohands.article.MemberArticle;
import com.be.dohands.article.dto.ArticleSlice;
import com.be.dohands.article.dto.CreateArticleDto;
import com.be.dohands.article.repository.ArticleQueryRepository;
import com.be.dohands.article.repository.ArticleRepository;
import com.be.dohands.article.repository.MemberArticleRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.notification.data.NotificationType;
import com.be.dohands.notification.dto.NotificationDto;
import com.be.dohands.notification.service.FcmService;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleQueryRepository articleQueryRepository;
    private final MemberRepository memberRepository;
    private final MemberArticleRepository memberArticleRepository;

    private final FcmService fcmService;

    @Transactional
    public void saveArticle(CreateArticleDto dto) {
        Article article = new Article(dto.title(), dto.content());
        articleRepository.save(article);
//        fcmService.send(NotificationDto.builder()
//                        .notificationType(ARTICLE)
//                        .build()
//        );
    }

    @Transactional(readOnly = true)
    public ArticleSlice findArticles(String loginId, String next, int size) {
        if (next.isEmpty()) next = null;
        Long nextId = next == null ? null : Long.parseLong(next);
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        ArticleSlice articles = articleQueryRepository.findAllArticles(nextId, size);
        articles.getItems()
                .forEach(item -> {
                    if (memberArticleRepository.findMemberArticleByUserIdAndAndArticleId(member.getUserId(),
                            item.getArticleId()).isPresent()) {
                        item.updateRead();
                    }
                });

        return articles;
    }

    @Transactional
    public void readArticle(String loginId, Long articleId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        Optional<MemberArticle> oMemberArticle = memberArticleRepository.findMemberArticleByUserIdAndAndArticleId(
                member.getUserId(), articleId);

        if (oMemberArticle.isEmpty()) {
            memberArticleRepository.save(new MemberArticle(member.getUserId(), articleId));
        }
    }
}
