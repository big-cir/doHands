package com.be.dohands.badge.service;

import com.be.dohands.badge.Badge;
import com.be.dohands.badge.MemberBadge;
import com.be.dohands.badge.repository.BadgeRepository;
import com.be.dohands.badge.repository.MemberBadgeRepository;
import com.be.dohands.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final MemberRepository memberRepository;
    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;


    @Transactional
    public List<Badge> findMemberBadges(String loginId) {
        Long userId = memberRepository.findByLoginId(loginId).get().getUserId();
        return memberBadgeRepository.findMemberBadgesByUserId(userId).stream()
                .map(MemberBadge::getBadgeId)
                .map(this::findBadges)
                .toList();
    }

    private Badge findBadges(Long badgeId) {
        return badgeRepository.findById(badgeId).get();
    }
}
