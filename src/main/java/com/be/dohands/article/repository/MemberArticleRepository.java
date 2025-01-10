package com.be.dohands.article.repository;

import com.be.dohands.article.MemberArticle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberArticleRepository extends JpaRepository<MemberArticle, Long> {

    Optional<MemberArticle> findMemberArticleByUserIdAndAndArticleId(Long userId, Long articleId);
}
