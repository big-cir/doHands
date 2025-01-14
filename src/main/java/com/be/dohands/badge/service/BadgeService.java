package com.be.dohands.badge.service;

import com.be.dohands.badge.Badge;
import com.be.dohands.badge.BadgeResponseDTO;
import com.be.dohands.badge.BadgeResponseDTO.BadgeInfo;
import com.be.dohands.badge.MemberBadge;
import com.be.dohands.badge.repository.BadgeRepository;
import com.be.dohands.badge.repository.MemberBadgeRepository;
import com.be.dohands.evaluation.repository.EvaluationExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.repository.UserQuestRepository;
import com.be.dohands.tf.repository.TfExpRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final MemberRepository memberRepository;
    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final UserQuestRepository userQuestRepository;
    private final EvaluationExpRepository evaluationExpRepository;
    private final TfExpRepository tfExpRepository;


    @Transactional
    public BadgeResponseDTO findMemberBadges(String username) {
        Long userId = memberRepository.findByLoginId(username).get().getUserId();
        Set<Long> set = new HashSet<>(memberBadgeRepository.findMemberBadgeIdsByUserId(userId));

        List<BadgeInfo> result = badgeRepository.findAll().stream().map(badge -> {

            boolean acheieved = set.contains(badge.getId());
            return BadgeInfo.builder()
                .badgeId(badge.getId())
                .name(badge.getName())
                .condition(badge.getCondition())
                .acheived(acheieved)
                .build();
        }).collect(Collectors.toList());


        return BadgeResponseDTO.builder()
            .badgeInfoList(result)
            .build();
    }

    @Transactional
    public void updateBadge(Long userId, Integer year){

        Set<Long> set = new HashSet<>(memberBadgeRepository.findMemberBadgeIdsByUserId(userId));

        for (Long i = 0L; i < 9L; i++){
            if (set.contains(i)) continue;

            // 처음 경험치를 획득했을 때
            if (i == 0) {
                Integer count = userQuestRepository.countIdsByUserIdAndStatus(userId, StatusType.DONE);
                if (count >= 1 && count < 20) {
                    memberBadgeRepository.save(new MemberBadge(userId, i));
                }
            }
            // 상,하반기 인사평가 S
            else if (i == 1) {
                if (evaluationExpRepository.countIdsByUserIdAndYear(userId, year) == 2){
                    memberBadgeRepository.save(new MemberBadge(userId,i));
                }
            }
            // 퀘스트 n개 달성
            else if (i >= 2 && i <= 5) {
                Integer count = userQuestRepository.countIdsByUserIdAndStatus(userId, StatusType.DONE);
                if (i == 2 && count >= 20 && count < 50) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 3 && count >= 50 && count < 80) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 4 && count >= 80 && count < 100) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 5 && count >= 100) memberBadgeRepository.save(new MemberBadge(userId,i));
            }
            // TF n개 달성
            else {
                Integer count = tfExpRepository.countIdsByUserId(userId);
                if (i == 6 && count >= 3 && count < 5) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 7 && count >= 5 && count < 10) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 8 && count >= 10) memberBadgeRepository.save(new MemberBadge(userId,i));
            }
        }
    }


    private Badge findBadges(Long badgeId) {
        return badgeRepository.findById(badgeId).get();
    }
}
