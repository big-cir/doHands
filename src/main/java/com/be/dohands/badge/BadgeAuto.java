package com.be.dohands.badge;

import com.be.dohands.badge.repository.BadgeRepository;
import com.be.dohands.badge.repository.MemberBadgeRepository;
import com.be.dohands.evaluation.repository.EvaluationExpRepository;
import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.repository.UserQuestRepository;
import com.be.dohands.tf.repository.TfExpRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BadgeAuto {

    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final UserQuestRepository userQuestRepository;
    private final EvaluationExpRepository evaluationExpRepository;
    private final TfExpRepository tfExpRepository;

//    @Transactional
//    @PostConstruct
//    public void initializeBadge(){
//
//        List<Badge> initializeList = new ArrayList<>();
//        initializeList.add(new Badge("천리길도 한 걸음부터", "첫 경험치 획득"));
//        initializeList.add(new Badge("노력은 배신하지 않는다", "상반기, 하반기 인사평가 S등급"));
//        initializeList.add(new Badge("초심자의 힘", "퀘스트 20개 완료"));
//        initializeList.add(new Badge("지속적인 도전 정신", "퀘스트 50개 완료"));
//        initializeList.add(new Badge("퀘스트 달인", "퀘스트 80개 완료"));
//        initializeList.add(new Badge("무한 성장", "퀘스트 100개 완료"));
//        initializeList.add(new Badge("팀워크의 첫 발걸음", "TF 3개 참여"));
//        initializeList.add(new Badge("팀워크의 달인", "TF 5개 참여"));
//        initializeList.add(new Badge("팀워크의 마스터", "TF 10개 참여"));
//
//        badgeRepository.saveAllAndFlush(initializeList);
//    }


    @Transactional
    public void updateBadge(Long userId, Integer year){

        Set<Long> set = new HashSet<>(memberBadgeRepository.findMemberBadgeIdsByUserId(userId));

        for (Long i = 1L; i <= 9L; i++){
            if (set.contains(i)) continue;

            // 처음 경험치를 획득했을 때
            if (i == 1) {
                Integer count = userQuestRepository.countIdsByUserIdAndStatus(userId, StatusType.DONE);
                if (count >= 1 && count < 20) {
                    memberBadgeRepository.save(new MemberBadge(userId, i));
                }
            }
            // 상,하반기 인사평가 S
            else if (i == 2) {
                if (evaluationExpRepository.countIdsByUserIdAndYear(userId, year) == 2){
                    memberBadgeRepository.save(new MemberBadge(userId,i));
                }
            }
            // 퀘스트 n개 달성
            else if (3 <= i && i <= 6) {
                Integer count = userQuestRepository.countIdsByUserIdAndStatus(userId, StatusType.DONE);
                if (i == 3 && count >= 20 && count < 50) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 4 && 50 <= count && count < 80) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 5 && 80 <= count && count < 100) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 6 && 100 <= count) memberBadgeRepository.save(new MemberBadge(userId,i));
            }
            // TF n개 달성
            else {
                Integer count = tfExpRepository.countIdsByUserId(userId);
                if (i == 7 && 3 <= count && count < 5) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 8 && 5 <= count && count < 10) memberBadgeRepository.save(new MemberBadge(userId,i));
                else if (i == 9 && 10 <= count) memberBadgeRepository.save(new MemberBadge(userId,i));
            }
        }
    }
}
