package com.be.dohands.badge.service;

import com.be.dohands.badge.Badge;
import com.be.dohands.badge.BadgeResponseDTO;
import com.be.dohands.badge.BadgeResponseDTO.BadgeInfo;
import com.be.dohands.badge.repository.BadgeRepository;
import com.be.dohands.badge.repository.MemberBadgeRepository;
import com.be.dohands.member.repository.MemberRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final MemberRepository memberRepository;
    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;


    @Transactional
    public BadgeResponseDTO findMemberBadges(String username) {
        Long userId = memberRepository.findByLoginId(username).get().getUserId();
        Set<Long> set = new HashSet<>(memberBadgeRepository.findMemberBadgeIdsByUserId(userId));

        List<BadgeInfo> result = badgeRepository.findAll().stream().map(badge -> {
            boolean achieved = set.contains(badge.getId());
            return BadgeInfo.builder()
                .badgeId(badge.getId())
                .name(badge.getName())
                .condition(badge.getCondition())
                .achieved(achieved)
                .build();
        }).collect(Collectors.toList());

        return BadgeResponseDTO.builder()
            .badgeInfoList(result)
            .build();
    }

    private Badge findBadges(Long badgeId) {
        return badgeRepository.findById(badgeId).get();
    }
}
