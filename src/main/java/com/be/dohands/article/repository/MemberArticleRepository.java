package com.be.dohands.article.repository;

import com.be.dohands.article.MemberArticle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberArticleRepository extends JpaRepository<MemberArticle, Long> {

    Optional<MemberArticle> findMemberArticleByUserIdAndAndArticleId(Long userId, Long articleId);

    @Query(value = "select cast(coalesce(count(ma.userArticleId),0) as int) from MemberArticle ma where ma.userId = :userId")
    Integer countUnreadArticles(@Param("userId") Long userId);
}
