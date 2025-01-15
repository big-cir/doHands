package com.be.dohands.common.schedules;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.repository.LevelExpRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LevelUp {

    private final MemberExpRepository memberExpRepository;
    private final LevelExpRepository levelExpRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void updateLevel(){
        List<MemberExp> memberExps = memberExpRepository.findAll();

        for (MemberExp memberExp : memberExps) {

            Member member = memberRepository.findByUserId(memberExp.getUserId());
            Integer nextCumulative = memberExp.getCumulativeExp() + memberExp.getCurrentExp();
            Integer levelTotalCnt = levelExpRepository.countLevelIdsByJobCategory(member.getJobCategory());

            int memberLevel = Integer.parseInt(levelExpRepository.findByLevelExpId(member.getLevelId()).getName().split(" ")[1]);

            if (memberLevel >= levelTotalCnt) { updateExps(memberExp, nextCumulative); return; }

            String nextLevelName = member.getJobCategory() + " " + (memberLevel+1);
            LevelExp nextLevel = levelExpRepository.findByName(nextLevelName);

            if (nextCumulative >= nextLevel.getExp()) {
                member.setLevelId(levelExpRepository.findLevelExpByName(nextLevelName).get().getLevelExpId());
                updateExps(memberExp, nextCumulative);
            }

            memberRepository.save(member);
        }

    }

    private void updateExps(MemberExp memberExp, Integer nextCumulative){
        memberExp.setCumulativeExp(nextCumulative);
        memberExp.setCurrentExp(0);
        memberExpRepository.save(memberExp);
    }
}
