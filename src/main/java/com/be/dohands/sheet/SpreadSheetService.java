package com.be.dohands.sheet;

import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpreadSheetService {

    private final MemberProcessor memberProcessor;
    private final ArticleProcessor articleProcessor;
    private final TfExpProcessor tfExpProcessor;
    private final JobQuestProcessor jobQuestProcessor;
    private final LevelExpProcessor levelExpProcessor;

    private final MemberRepository memberRepository;



    public void readAndUpdateMemberSheet(Map<String, Object> payload) {
        memberProcessor.readSheetAndUpdateDb(payload);
    }

    public void readAndUpdateArticleSheet(Map<String, Object> payload) {
        articleProcessor.readSheetAndUpdateDb(payload);
    }

    public void readAndUpdateTfExpSheet(Map<String, Object> payload) {
        tfExpProcessor.readSheetAndUpdateDb(payload);
    }

    public void readAndUpdateJobRequestSheet(Map<String, Object> payload) {
        jobQuestProcessor.readDividedSheetAndUpdateDb(payload);
    }

    public void readAndUpdateLevelExpSheet(Map<String, Object> payload) {
        levelExpProcessor.readSheetAndUpdateDb(payload);
    }


    public void changeMemberPassword(String spreadsheetId, String password, Long userId)
        throws GeneralSecurityException, IOException {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 구성원입니다"));

        member.updatePassword(password);
        memberRepository.save(member);

        String sheetName = "참고. 구성원 정보";
        String sheetLocation = sheetName + "!J" + member.getSheetRow();

        List<List<Object>> values = new ArrayList<>();
        values.add(Collections.singletonList(password));

        memberProcessor.updateValues(spreadsheetId, sheetLocation, "RAW", values);
    }

}
