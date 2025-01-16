package com.be.dohands.sheet;

import static com.be.dohands.notification.data.NotificationType.ARTICLE;

import com.be.dohands.evaluation.EvaluationExp;
import com.be.dohands.level.repository.LevelExpRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberExpService;
import com.be.dohands.notification.dto.NotificationDto;
import com.be.dohands.notification.service.FcmService;
import com.be.dohands.quest.entity.JobQuestEntity;
import com.be.dohands.quest.entity.LeaderQuestExpEntity;
import com.be.dohands.tf.TfExp;
import com.be.dohands.tf.repository.TfExpRepository;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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
    private final LeaderQuestProcessor leaderQuestProcessor;
    private final EvaluationProcessor evaluationProcessor;

    private final MemberRepository memberRepository;
    private final LevelExpRepository levelExpRepository;
    private final MemberExpRepository memberExpRepository;
    private final MemberExpService memberExpService;
    private final FcmService fcmService;
    private final TfExpRepository tfExpRepository;


    public void readAndUpdateMemberSheet(Map<String, Object> payload) {
        List<TransformResult<Member>> transformResults = memberProcessor.readSheetAndUpdateDb(payload);
        transformResults.stream()
                .map(TransformResult::getEntity)
                .forEach(m -> {
                    int year = LocalDate.now().getYear();
                    memberExpRepository.findByUserId(m.getUserId())
                            .orElseGet(() -> memberExpRepository.save(new MemberExp(year, m.getUserId())));
                });
    }

    public void readAndUpdateArticleSheet(Map<String, Object> payload) {
        articleProcessor.readSheetAndUpdateDb(payload);
        fcmService.send(new NotificationDto(null, null, ARTICLE));
    }

    public void readAndUpdateTfExpSheet(Map<String, Object> payload) {
        Set<String> set = tfExpProcessor.readSheetAndUpdateDb(payload).stream()
                .filter(TransformResult::isNotificationYn)
                .map(TransformResult::getEntity)
                .filter(Objects::nonNull)
                .map(TfExp::getEmployeeNumber)
                .collect(Collectors.toSet());

        CompletableFuture.runAsync(() -> {
            set.forEach(en -> memberExpService.findCompleteQuestWithOutJob("tf", en));
        });
    }

    public void readAndUpdateJobRequestSheet(Map<String, Object> payload) {
        List<String> list = jobQuestProcessor.readDividedSheetAndUpdateDb(payload).stream()
                .filter(TransformResult::isNotificationYn)
                .map(TransformResult::getEntity)
                .filter(result -> result instanceof JobQuestEntity)
                .map(result -> ((JobQuestEntity) result).getDepartment())
                .collect(Collectors.toList());

        CompletableFuture.runAsync(() -> {
           list.forEach(memberExpService::findCompleteJobQuest);
        });
    }

    public void readAndUpdateLeaderRequestSheet(Map<String, Object> payload) {
        Set<String> set = leaderQuestProcessor.readDividedSheetAndUpdateDb(payload).stream()
                .filter(TransformResult::isNotificationYn)
                .map(TransformResult::getEntity)
                .filter(result -> result instanceof LeaderQuestExpEntity)
                .map(result -> ((LeaderQuestExpEntity) result).getEmployeeNumber())
                .collect(Collectors.toSet());

        CompletableFuture.runAsync(() -> {
            set.forEach(en -> memberExpService.findCompleteQuestWithOutJob("leader", en));
        });
    }

    public void readAndUpdateLevelExpSheet(Map<String, Object> payload) {
        levelExpProcessor.readSheetAndUpdateDb(payload);
    }

    public void readAndUpdateEvaluationExpSheet(Map<String, Object> payload) {
        Set<String> set = evaluationProcessor.readDividedSheetAndUpdateDb(payload).stream()
                .filter(TransformResult::isNotificationYn)
                .map(TransformResult::getEntity)
                .filter(result -> result instanceof EvaluationExp)
                .map(result -> ((EvaluationExp) result).getEmployeeNumber())
                .collect(Collectors.toSet());

        CompletableFuture.runAsync(() -> {
            set.forEach(en -> memberExpService.findCompleteQuestWithOutJob("evaluation", en));
        });
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

    public void changeMemberInfos(String spreadsheetId, Member member)
        throws GeneralSecurityException, IOException {

        String sheetName = "참고. 구성원 정보";
        String sheetLocation = sheetName + "!C" + member.getSheetRow() + ":J" + member.getSheetRow() ;

        String levelName = levelExpRepository.findById(member.getLevelId())
            .map(levelExp -> levelExp.getName())
            .orElseThrow(() -> new NoSuchElementException("존재하지않는 레벨ID"));

        List<List<Object>> values = new ArrayList<>();
        values.add(List.of(member.getName(),DateUtil.localDateToString(member.getHireDate()), member.getDepartment(), member.getJobGroup(), levelName, member.getLoginId(), "1234", member.getPassword()));

        memberProcessor.updateValues(spreadsheetId, sheetLocation, "RAW", values);
    }

    /**
     * admin에서 등록한 계정 정보 시트에 자동 추가하는 메서드
     */
    public void createMemberInfoToSheet(String spreadsheetId, Member member)
        throws GeneralSecurityException, IOException {

        String sheetName = "참고. 구성원 정보";
        String sheetLocation = sheetName + "!B:J";

        List<List<Object>> values = new ArrayList<>();
        String levelName = levelExpRepository.findById(member.getLevelId())
            .map(levelExp -> levelExp.getName())
            .orElseThrow(() -> new NoSuchElementException("존재하지않는 레벨ID"));

        values.add(List.of(member.getEmployeeNumber(), member.getName(),DateUtil.localDateToString(member.getHireDate()), member.getDepartment(), member.getJobGroup(), levelName, member.getLoginId(), member.getPassword()));

        AppendValuesResponse response = memberProcessor.appendValues(spreadsheetId, sheetLocation, "RAW", values);
        // 스프레드시트에 저장된 행 가져와서 member sheetRow 업데이트
        Integer sheetRow = Integer.parseInt(response.getUpdates().getUpdatedRange().replaceAll(".*?(\\d+).*", "$1"));
        member.updateSheetRow(sheetRow);
        memberRepository.save(member);
    }

    /**
     * admin에서 등록한 계정 정보 시트에 자동 추가하는 메서드 for test
     */
    @Deprecated
    public void createMemberInfoToSheetForTest(String spreadsheetId)
        throws GeneralSecurityException, IOException {

        Member member = Member.builder()
            .loginId("김테스_두핸즈")
            .password("1234")
            .employeeNumber("2024011588")
            .department("음성 1센터")
            .name("김테스")
            .levelId(5L)
            .hireDate(LocalDate.of(2024, 1, 15))
            .jobGroup("2")
            .jobCategory("관리직")
            .build();

        memberRepository.save(member);

        String sheetName = "참고. 구성원 정보";
        String sheetLocation = sheetName + "!J:J";

        List<List<Object>> values = new ArrayList<>();
        String levelName = levelExpRepository.findById(member.getLevelId())
            .map(levelExp -> levelExp.getName())
            .orElseThrow(() -> new NoSuchElementException("존재하지않는 레벨ID"));

        values.add(List.of(member.getEmployeeNumber(), member.getName(),DateUtil.localDateToString(member.getHireDate()), member.getDepartment(), member.getJobGroup(), levelName, member.getLoginId(), member.getPassword()));

        AppendValuesResponse response = memberProcessor.appendValues(spreadsheetId, sheetLocation, "RAW", values);
        // 스프레드시트에 저장된 행 가져와서 member sheetRow 업데이트
        Integer sheetRow = Integer.parseInt(response.getUpdates().getUpdatedRange().replaceAll(".*?(\\d+).*", "$1"));
        member.updateSheetRow(sheetRow);
        memberRepository.save(member);
    }



}
